import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class WebPage {
    public String url;
    public String title;
    public String content;

    public WebPage(String url, String title) {
        this.url = url;
        this.title = title;
        this.content = fetchContent(); // 建立時自動抓取內容
    }

    // 簡單的爬蟲邏輯 (對應 HTMLHandler)
    private String fetchContent() {
        try {
            // 模擬瀏覽器 User-Agent 避免被擋
            URL urlObj = new URL(this.url);
            URLConnection conn = urlObj.openConnection();
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");
            conn.setConnectTimeout(3000); // 設定超時
            conn.setReadTimeout(3000);
            
            InputStream in = conn.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            StringBuilder retVal = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                retVal.append(line).append("\n");
            }
            return retVal.toString();
        } catch (Exception e) {
            // 抓取失敗時回傳空字串，避免程式崩潰
            return "";
        }
    }

    // 計算分數：Boyer-Moore 或簡單 contains (這裡用簡單版以示範邏輯)
    public double calculateScore(ArrayList<Keyword> keywords) {
        double score = 0;
        if (content.isEmpty()) return 0;
        
        String upperContent = content.toUpperCase();
        for (Keyword k : keywords) {
            String upperKeyword = k.name.toUpperCase();
            int lastIndex = 0;
            int count = 0;
            while ((lastIndex = upperContent.indexOf(upperKeyword, lastIndex)) != -1) {
                count++;
                lastIndex += upperKeyword.length();
            }
            score += count * k.weight;
        }
        return score;
    }
}