package net.dontdrinkandroot.gitki.wicket.component.modal;

import net.dontdrinkandroot.gitki.model.FilePath;
import net.dontdrinkandroot.gitki.model.Role;
import net.dontdrinkandroot.gitki.service.git.GitService;
import net.dontdrinkandroot.gitki.wicket.GitkiWebSession;
import net.dontdrinkandroot.gitki.wicket.event.FileDeletedEvent;
import net.dontdrinkandroot.gitki.wicket.security.Instantiate;
import net.dontdrinkandroot.wicket.behavior.OnClickScriptBehavior;
import net.dontdrinkandroot.wicket.bootstrap.behavior.ButtonBehavior;
import net.dontdrinkandroot.wicket.bootstrap.component.button.SubmitLabelButton;
import net.dontdrinkandroot.wicket.bootstrap.component.form.formgroup.FormGroupInputText;
import net.dontdrinkandroot.wicket.bootstrap.component.modal.AjaxFormModal;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.IOException;

/**
 * @author Philip Washington Sorst <philip@sorst.net>
 */
@Instantiate(Role.COMMITTER)
public class RemoveFileModal extends AjaxFormModal<FilePath>
{
    @SpringBean
    private GitService gitService;

    private IModel<String> commitMessageModel;

    public RemoveFileModal(String id, IModel<FilePath> model)
    {
        super(id, model);
    }

    @Override
    protected IModel<String> createHeadingModel()
    {
        return Model.of("Confirm File Removal");
    }

    @Override
    protected void populateFormGroups(RepeatingView formGroupView)
    {
        super.populateFormGroups(formGroupView);

        this.commitMessageModel = Model.of("Removing " + this.getModelObject().toAbsoluteString());

        FormGroupInputText formGroupCommitMessage =
                new FormGroupInputText(formGroupView.newChildId(), Model.of("Commit Message"), this.commitMessageModel);
        formGroupCommitMessage.addDefaultAjaxInputValidation();
        formGroupCommitMessage.setRequired(true);
        formGroupView.add(formGroupCommitMessage);
    }

    @Override
    protected void populateFormActions(RepeatingView formActionView)
    {
        super.populateFormActions(formActionView);

        formActionView.add(new SubmitLabelButton(formActionView.newChildId(), new StringResourceModel("gitki.remove")));

        Label cancelButton = new Label(formActionView.newChildId(), "Cancel");
        cancelButton.add(new ButtonBehavior());
        cancelButton.add(new OnClickScriptBehavior(this.getHideScript()));
        formActionView.add(cancelButton);
    }

    protected void onFileDeleted(AjaxRequestTarget target, FilePath path)
    {
        this.send(this.getPage(), Broadcast.BREADTH, new FileDeletedEvent(this.getModelObject(), target));
    }

    @Override
    protected void onSubmit(AjaxRequestTarget target)
    {
        super.onSubmit(target);

        try {
            RemoveFileModal.this.gitService.removeAndCommit(
                    RemoveFileModal.this.getModelObject(),
                    GitkiWebSession.get().getUser(),
                    RemoveFileModal.this.commitMessageModel.getObject()
            );
        } catch (IOException | GitAPIException e) {
            throw new WicketRuntimeException(e);
        }
    }

    @Override
    protected void onAfterSubmit(AjaxRequestTarget target)
    {
        super.onAfterSubmit(target);

        RemoveFileModal.this.onFileDeleted(target, RemoveFileModal.this.getModelObject());
    }
}
