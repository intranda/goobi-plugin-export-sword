package de.intranda.goobi.plugins;

import java.io.IOException;

import lombok.Data;
import lombok.EqualsAndHashCode;

import org.goobi.beans.Process;
import org.goobi.production.enums.PluginType;
import org.goobi.production.plugin.interfaces.IExportPlugin;
import org.goobi.production.plugin.interfaces.IPlugin;

import ugh.exceptions.DocStructHasNoTypeException;
import ugh.exceptions.MetadataTypeNotAllowedException;
import ugh.exceptions.PreferencesException;
import ugh.exceptions.ReadException;
import ugh.exceptions.TypeNotAllowedForParentException;
import ugh.exceptions.WriteException;
import de.sub.goobi.export.download.ExportMets;
import de.sub.goobi.helper.exceptions.DAOException;
import de.sub.goobi.helper.exceptions.ExportFileException;
import de.sub.goobi.helper.exceptions.SwapException;
import de.sub.goobi.helper.exceptions.UghHelperException;
import net.xeoh.plugins.base.annotations.PluginImplementation;

@PluginImplementation
@EqualsAndHashCode(callSuper = false)
public @Data class MycoreExportPlugin extends ExportMets implements IExportPlugin, IPlugin {

    private static final String PLUGIN_NAME = "plugin_intranda_mycore_export";

    private static final String DESTINATION = "/opt/digiverso/viewer/hotfolder/";

    @Override
    public boolean startExport(Process process) throws IOException, InterruptedException, DocStructHasNoTypeException, PreferencesException,
            WriteException, MetadataTypeNotAllowedException, ExportFileException, UghHelperException, ReadException, SwapException, DAOException,
            TypeNotAllowedForParentException {
        return startExport(process, "");
    }

    @Override
    public boolean startExport(Process process, String destination) throws IOException, InterruptedException, DocStructHasNoTypeException,
            PreferencesException, WriteException, MetadataTypeNotAllowedException, ExportFileException, UghHelperException, ReadException,
            SwapException, DAOException, TypeNotAllowedForParentException {
        myPrefs = process.getRegelsatz().getPreferences();
        String metsFileName = DESTINATION + process.getId() + "_mets.xml";
        return writeMetsFile(process, metsFileName, process.readMetadataFile(), false);
    }

    @Override
    public PluginType getType() {
        return PluginType.Export;
    }

    @Override
    public String getTitle() {
        return PLUGIN_NAME;
    }

    public String getDescription() {
        return PLUGIN_NAME;
    }

    @Override
    public void setExportFulltext(boolean exportFulltext) {

    }

    @Override
    public void setExportImages(boolean exportImages) {

    }

}
