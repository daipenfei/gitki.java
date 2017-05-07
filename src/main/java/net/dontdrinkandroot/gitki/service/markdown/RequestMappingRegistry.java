package net.dontdrinkandroot.gitki.service.markdown;

import net.dontdrinkandroot.gitki.wicket.page.file.edit.EditPage;
import net.dontdrinkandroot.gitki.wicket.page.file.view.ViewPage;

import java.util.Map;

/**
 * @author Philip Washington Sorst <philip@sorst.net>
 */
public interface RequestMappingRegistry
{
    void setViewMappings(Map<String, Class<? extends ViewPage>> mappings);

    void setEditMappings(Map<String, Class<? extends EditPage>> mappings);

    void addViewMapping(String extension, Class<? extends ViewPage> pageClass);

    void addEditMapping(String extension, Class<? extends EditPage> pageClass);

    Class<? extends ViewPage> resolveViewMapping(String extension);

    Class<? extends EditPage> resolveEditMapping(String extension);
}