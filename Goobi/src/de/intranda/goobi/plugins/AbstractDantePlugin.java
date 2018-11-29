package de.intranda.goobi.plugins;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.lang.StringUtils;
import org.goobi.production.plugin.interfaces.AbstractMetadataPlugin;
import org.goobi.production.plugin.interfaces.IMetadataPlugin;

import de.intranda.digiverso.normdataimporter.dante.DanteImport;
import de.intranda.digiverso.normdataimporter.model.NormData;
import de.intranda.digiverso.normdataimporter.model.NormDataRecord;
import de.sub.goobi.config.ConfigPlugins;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.xeoh.plugins.base.annotations.PluginImplementation;

@PluginImplementation
@EqualsAndHashCode(callSuper = false)
@Data
public abstract class AbstractDantePlugin extends AbstractMetadataPlugin implements IMetadataPlugin {

    private String searchValue;
    private String searchOption;

    @Deprecated
    private List<List<NormData>> dataList;
    @Deprecated
    private List<NormData> currentData;

    private List<NormDataRecord> normdataList;
    private NormDataRecord selectedRecord;

    private boolean showNotHits = false;

    @Override
    public String getPagePath() {
        return "/uii/includes/metseditor/extension/formMetsGndInput.xhtml";
    }

    // name of database
    public abstract String getVocabulary();

    // class name for goobi
    @Override
    public abstract String getTitle();

    // name of main field containing the representative value
    @Deprecated
    public abstract String getLabel();



    // gets list with ordered names of main fields to get the representative value
    // can be overwritten within individual plugins, otherwise the default configuration is used
    public List<String> getLabelList() {
        List<String> orderedList = null;
        try {
            XMLConfiguration config = ConfigPlugins.getPluginConfig("metadata-dante");
            orderedList = config.getList("fieldName");
        } catch (Exception e) {
            // nothing is configured, use default values from getLabel()
        }
        return orderedList;
    }



    @Override
    public String search() {
        StringBuilder url = new StringBuilder();
        url.append("http://api.dante.gbv.de/search?");
        url.append("voc=");
        url.append(getVocabulary());
        url.append("&limit=20");
        url.append("&cache=0");
        url.append("&query=");
        url.append(getSearchValue());
        normdataList = DanteImport.importNormDataList(url.toString(), getLabelList());
        if (normdataList.isEmpty()) {
            showNotHits = true;
        } else {
            showNotHits = false;
        }

        return "";
    }

    @Override
    public String getData() {

        if (StringUtils.isNotBlank(selectedRecord.getPreferredValue())) {
            metadata.setValue(selectedRecord.getPreferredValue());
        }

        for (NormData normdata : selectedRecord.getNormdataList()) {
            if (normdata.getKey().equals("URI")) {
                metadata.setAutorityFile("dante", "https://uri.gbv.de/terminology/dante/", normdata.getValues().get(0).getText());
            }
            else if (StringUtils.isBlank(selectedRecord.getPreferredValue()) &&  CollectionUtils.isEmpty(getLabelList()) && normdata.getKey().equals(getLabel())) {
                String value = normdata.getValues().get(0).getText();
                metadata.setValue(filter(value));
            }
        }

        if (StringUtils.isBlank(selectedRecord.getPreferredValue()) && CollectionUtils.isNotEmpty(getLabelList())) {
            findPrioritisedMetadata: for (String fieldName : getLabelList()) {
                for (NormData normdata : currentData) {
                    if (normdata.getKey().equals(fieldName)) {
                        String value = normdata.getValues().get(0).getText();
                        metadata.setValue(filter(value));
                        break findPrioritisedMetadata;
                    }
                }
            }
        }

        normdataList = new ArrayList<>();
        return "";
    }

    @Override
    public boolean isShowNoHitFound() {
        return showNotHits;
    }


    @Override
    public boolean isDisableIdentifierField() {
        return true;
    }

    @Override
    public boolean isDisableMetadataField() {
        return true;
    }
}
