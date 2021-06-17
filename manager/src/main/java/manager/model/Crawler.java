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
import java.util.LinkedList;
import java.util.List;

public class Crawler{
    private Crawler(){
    }

    public static SearchResults search(final String query) throws FailingHttpStatusCodeException, ElementNotFoundException, IOException{
        SearchResults.Status status = SearchResults.Status.OK;
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
            return new SearchResults(status);
        }
        final HtmlTable table = page.getFirstByXPath("//table[@id='table-sortable']");
        if(table == null){
            throw new ElementNotFoundException("table", "id", "table-sortable");
        }
        List<SteamApp> steamApps = new LinkedList<>();
        for(final HtmlTableBody body : table.getBodies()){
            if(!body.hasAttribute("hidden")){
                status = SearchResults.Status.WARN;
                Logger.warn("result table is missing \"hidden\" attribute");
            }
            List<HtmlTableRow> rows = body.getRows();
            for(final HtmlTableRow row : rows){
                List<HtmlTableCell> cols = row.getCells();
                if(cols.size() != 4){
                    status = SearchResults.Status.WARN;
                    Logger.warn("result table has unexpected amount of columns");
                    break;
                }
                if(!append(steamApps, cols.get(0).asNormalizedText().trim(), cols.get(2).asNormalizedText().trim(), cols.get(1).asNormalizedText().trim())){
                    status = SearchResults.Status.WARN;
                }
            }
        }
        return new SearchResults(status, steamApps);
    }

    private static String buildUrl(String query){
        try{
            return "https://steamdb.info/search/?a=app&q=" + URLEncoder.encode(query.trim(),
                    StandardCharsets.UTF_8.toString());
        }catch(UnsupportedEncodingException ex){
            throw new RuntimeException(ex);
        }
    }

    private static boolean append(List<SteamApp> steamApps, String appId, String title, String type){
        if(!NumberUtils.isParsable(appId)){
            Logger.warn("appId is not a number");
            return false;
        }
        if(title.length() < 1){
            Logger.warn("empty title");
            return false;
        }
        if(!SteamApp.Type.contains(type)){
            Logger.info("unsupported type skipped ({})", type);
            return true;
        }
        steamApps.add(new SteamApp(Long.parseLong(appId), title, SteamApp.Type.fromString(type)));
        return true;
    }
}
