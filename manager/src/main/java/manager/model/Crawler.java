package manager.model;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableBody;
import com.gargoylesoftware.htmlunit.html.HtmlTableCell;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;
import org.apache.commons.lang3.math.NumberUtils;
import org.tinylog.Logger;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Crawler{
    private Crawler(){
    }

    public static List<SteamApp> search(final String query) throws FailingHttpStatusCodeException, ElementNotFoundException, IOException{
        final HtmlPage page;
        try(final WebClient webClient = new WebClient(BrowserVersion.BEST_SUPPORTED)){
            webClient.getOptions().setCssEnabled(false);
            webClient.getOptions().setJavaScriptEnabled(false);
            webClient.getOptions().setTimeout(5 * 1000);
            // webClient.getOptions().setThrowExceptionOnScriptError(false);
            // webClient.setCssErrorHandler(new SilentCssErrorHandler());
            page = webClient.getPage(buildUrl(query));
        }
        if(page.getFirstByXPath("//div[@class='panel']/p[normalize-space() = 'Nothing was found matching your request']") != null){
            return Collections.emptyList();
        }
        final HtmlTable table = page.getFirstByXPath("//table[@id='table-sortable']");
        if(table == null){
            throw new ElementNotFoundException("table", "id", "table-sortable");
        }
        List<SteamApp> steamApps = new LinkedList<>();
        for(final HtmlTableBody body : table.getBodies()){
            if(!body.hasAttribute("hidden")){
                Logger.warn("result table is missing \"hidden\" attribute");
            }
            List<HtmlTableRow> rows = body.getRows();
            for(final HtmlTableRow row : rows){
                List<HtmlTableCell> cols = row.getCells();
                if(cols.size() != 4){
                    Logger.warn("result table has unexpected amount of columns");
                    break;
                }
                DomNode titleNode = cols.get(2).getFirstChild();
                if(titleNode == null){
                    Logger.warn("unexpected title html layout");
                    break;
                }
                append(steamApps, cols.get(0).asText(), titleNode.asText(), cols.get(1).asText());
            }
        }
        return steamApps;
    }

    private static String buildUrl(String query){
        try{
            return "https://steamdb.info/search/?a=app&q=" + URLEncoder.encode(query.trim(),
                    StandardCharsets.UTF_8.toString());
        }catch(UnsupportedEncodingException ex){
            throw new RuntimeException(ex);
        }
    }

    private static void append(List<SteamApp> steamApps, String appId, String title, String type){
        if(!NumberUtils.isParsable(appId)){
            Logger.warn("appId is not a number");
            return;
        }
        if(!SteamApp.Type.contains(type)){
            Logger.info("unsupported type skipped ({})", type);
            return;
        }
        steamApps.add(new SteamApp(Long.parseLong(appId), title, SteamApp.Type.fromString(type)));
    }
}
