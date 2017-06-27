package net.dontdrinkandroot.gitki.wicket.page.directory;

import net.dontdrinkandroot.gitki.model.DirectoryPath;
import net.dontdrinkandroot.gitki.service.git.GitService;
import net.dontdrinkandroot.gitki.wicket.component.DirectoryActionsDropdownButton;
import net.dontdrinkandroot.gitki.wicket.component.DirectoryEntriesPanel;
import net.dontdrinkandroot.gitki.wicket.model.DirectoryPathEntriesModel;
import net.dontdrinkandroot.gitki.wicket.page.BrowsePage;
import net.dontdrinkandroot.gitki.wicket.util.PageParameterUtils;
import net.dontdrinkandroot.wicket.bootstrap.css.FontAwesomeIconClass;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * @author Philip Washington Sorst <philip@sorst.net>
 */
public class DirectoryPage extends BrowsePage<DirectoryPath>
{
    @SpringBean
    private GitService gitService;

    public DirectoryPage(IModel<DirectoryPath> model)
    {
        super(model);
        PageParameterUtils.from(model.getObject(), this.getPageParameters());
    }

    public DirectoryPage(PageParameters parameters)
    {
        super(parameters);
        DirectoryPath path = PageParameterUtils.toDirectoryPath(parameters);
        this.setModel(Model.of(path));
    }

    @Override
    protected void onInitialize()
    {
        super.onInitialize();

        this.add(new DirectoryEntriesPanel("entries", new DirectoryPathEntriesModel(this.getModel())));
    }

    @Override
    protected void populatePrimaryButtons(RepeatingView view)
    {
        super.populatePrimaryButtons(view);

        DirectoryActionsDropdownButton directoryActionsButton =
                new DirectoryActionsDropdownButton(view.newChildId(), this.getModel());
        directoryActionsButton.getIconBehavior()
                .setAppendIcon(FontAwesomeIconClass.ELLIPSIS_V.createIcon().setFixedWidth(true));
        view.add(directoryActionsButton);
    }
}
