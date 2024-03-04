package de.intranda.goobi.plugins;

import java.io.IOException;

import org.goobi.beans.Process;
import org.goobi.production.enums.PluginType;
import org.goobi.production.plugin.interfaces.IExportPlugin;
import org.goobi.production.plugin.interfaces.IPlugin;

import de.sub.goobi.config.ConfigPlugins;
import de.sub.goobi.export.download.ExportMets;
import de.sub.goobi.helper.exceptions.DAOException;
import de.sub.goobi.helper.exceptions.ExportFileException;
import de.sub.goobi.helper.exceptions.SwapException;
import de.sub.goobi.helper.exceptions.UghHelperException;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.xeoh.plugins.base.annotations.PluginImplementation;
import ugh.exceptions.DocStructHasNoTypeException;
import ugh.exceptions.MetadataTypeNotAllowedException;
import ugh.exceptions.PreferencesException;
import ugh.exceptions.ReadException;
import ugh.exceptions.TypeNotAllowedForParentException;
import ugh.exceptions.WriteException;

@PluginImplementation
@EqualsAndHashCode(callSuper = false)
@Data
public class MycoreExportPlugin extends ExportMets implements IExportPlugin, IPlugin {

    private static final String PLUGIN_NAME = "intranda_export_sword";

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
        destination = ConfigPlugins.getPluginConfig(PLUGIN_NAME).getString("exportPath", "/opt/digiverso/viewer/hotfolder/");
        String metsFileName = destination + process.getId() + "_mets.xml";
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
