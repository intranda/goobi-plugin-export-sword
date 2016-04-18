package de.intranda.goobi.plugins;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.log4j.Log4j;

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
import de.schlichtherle.io.DefaultArchiveDetector;
import de.schlichtherle.io.File;
import de.sub.goobi.config.ConfigurationHelper;
import de.sub.goobi.export.download.ExportMets;
import de.sub.goobi.helper.NIOFileUtils;
import de.sub.goobi.helper.exceptions.DAOException;
import de.sub.goobi.helper.exceptions.ExportFileException;
import de.sub.goobi.helper.exceptions.SwapException;
import de.sub.goobi.helper.exceptions.UghHelperException;
import net.xeoh.plugins.base.annotations.PluginImplementation;

@PluginImplementation
@Log4j
@EqualsAndHashCode(callSuper = false)
public @Data class MycoreExportPlugin extends ExportMets implements IExportPlugin, IPlugin {

    private static final String PLUGIN_NAME = "plugin_intranda_mycore_export";

    private static final String DESTINATION = "/tmp/";

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
        if (log.isDebugEnabled()) {
            log.debug("started ingest to mycore interface for " + process.getTitel());
            log.debug("create zip file");
        }
        // 1.) zip erstellen

        Path zipFile = createZipFile(process);

        if (zipFile == null || !Files.exists(zipFile)) {
            // log, meldung
            return false;
        }

        // 2.) prüfen, ob es bereits eine ID gibt

        // 3.) wenn vorhanden, in Schnittstelle nach ID suchen

        // 4.) wenn vorhanden, dann zip + mets löschen

        // 5.) wenn keine ID, neues Objekt anlegen und ID in goobi speichern

        // 6.) mets und zip zu Objekt hochladen

        // 7.) alles in /tmp/name löschen

        return true;
    }

    private Path createZipFile(Process process) throws IOException, SwapException, DAOException, InterruptedException, PreferencesException,
            WriteException, TypeNotAllowedForParentException, ReadException {

        // create mets file
        String metsFileName = DESTINATION + process.getId() + "_mets.xml";
        writeMetsFile(process, metsFileName, process.readMetadataFile(), false);

        // TODO sicherstellen, dass in in projektconfig "" für "mycore" und für "alto" eingestellt ist

        // create zip

        File.setDefaultArchiveDetector(new DefaultArchiveDetector("tar.bz2|tar.gz|zip"));
        File ingestFile = new File(DESTINATION + process.getTitel() + "_" + process.getId() + ".zip");
        if (ingestFile.exists() && ingestFile.isArchive()) {
            // Empty archive if it already exists
            ingestFile.deleteAll();
        }

        File metsFile = new File(metsFileName);
        metsFile.copyTo(new File(ingestFile + java.io.File.separator + "mets.xml"));

        List<Path> imageNames = NIOFileUtils.listFiles(process.getImagesTifDirectory(false));
        for (Path imageName : imageNames) {
            File image = new File(imageName.toString());
            image.copyTo(new File(ingestFile + java.io.File.separator + image.getName()));
        }

        List<Path> altoNames = NIOFileUtils.listFiles(process.getAltoDirectory());
        if (altoNames != null && !altoNames.isEmpty()) {
            File altoFolder = new File(ingestFile + java.io.File.separator + "alto");
            for (Path altoName : altoNames) {
                File altoFile = new File(altoName.toString());
                altoFile.copyTo(new File(altoFolder + java.io.File.separator + altoFile.getName()));
            }
        }
        ingestFile.createNewFile();
        File.umount();

        return Paths.get(ingestFile.getAbsolutePath());
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
