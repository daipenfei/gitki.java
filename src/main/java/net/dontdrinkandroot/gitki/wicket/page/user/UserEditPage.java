package net.dontdrinkandroot.gitki.wicket.page.user;

import net.dontdrinkandroot.gitki.model.Role;
import net.dontdrinkandroot.gitki.model.User;
import net.dontdrinkandroot.gitki.service.user.UserService;
import net.dontdrinkandroot.gitki.wicket.security.Instantiate;
import net.dontdrinkandroot.wicket.bootstrap.component.button.SubmitButton;
import net.dontdrinkandroot.wicket.bootstrap.component.form.formgroup.FormGroupInputEmail;
import net.dontdrinkandroot.wicket.bootstrap.component.form.formgroup.FormGroupInputText;
import net.dontdrinkandroot.wicket.bootstrap.component.form.formgroup.FormGroupSelect;
import net.dontdrinkandroot.wicket.bootstrap.css.ButtonStyle;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import javax.inject.Inject;
import java.util.Arrays;

/**
 * @author Philip Washington Sorst <philip@sorst.net>
 */
@Instantiate(Role.WATCHER)
public class UserEditPage extends UserPage<User>
{
    @Inject
    private UserService userService;

    private IModel<String> newPasswordModel;

    public UserEditPage(PageParameters parameters)
    {
        super(parameters);
        User user = new User();
        user.setRole(Role.WATCHER);
        this.setModel(Model.of(user));
    }

    public UserEditPage(IModel<User> model)
    {
        super(model);
    }

    @Override
    protected IModel<String> createTitleModel()
    {
        return Model.of("Edit User");
    }

    @Override
    protected void onInitialize()
    {
        super.onInitialize();

        Form<User> form = new Form<>("form", this.getModel());
        this.add(form);

        FormGroupInputText formGroupFirstName = new FormGroupInputText(
                "firstName",
                Model.of("First Name"),
                new PropertyModel<>(this.getModel(), "firstName")
        );
        formGroupFirstName.setRequired(true);
        formGroupFirstName.addDefaultAjaxInputValidation();
        form.add(formGroupFirstName);

        FormGroupInputText formGroupLastName = new FormGroupInputText(
                "lastName",
                Model.of("Last Name"),
                new PropertyModel<>(this.getModel(), "lastName")
        );
        formGroupLastName.setRequired(true);
        formGroupLastName.addDefaultAjaxInputValidation();
        form.add(formGroupLastName);

        FormGroupInputEmail formGroupEmail =
                new FormGroupInputEmail("email", Model.of("Email"), new PropertyModel<>(this.getModel(), "email"));
        formGroupEmail.setRequired(true);
        formGroupEmail.addDefaultAjaxInputValidation();
        form.add(formGroupEmail);

        FormGroupSelect<Role> formGroupRole =
                new FormGroupSelect<Role>("role", Model.of("Role"), new PropertyModel<>(this.getModel(), "role"),
                        Arrays.asList(Role.values())
                );
        formGroupRole.setNullValid(false);
        formGroupRole.setRequired(true);
        form.add(formGroupRole);

        SubmitButton submitButton = new SubmitButton("submit", Model.of("Save"))
        {
            @Override
            public void onSubmit()
            {
                super.onSubmit();
                UserEditPage.this.userService.save(UserEditPage.this.getModelObject());
            }

            @Override
            public void onAfterSubmit()
            {
                super.onAfterSubmit();
                this.setResponsePage(UserListPage.class);
            }
        };
        submitButton.setButtonStyle(ButtonStyle.PRIMARY);
        form.add(submitButton);
    }
}