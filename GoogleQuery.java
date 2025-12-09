import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class GoogleQuery {
    // 請填入你的 API Key 和 CX
    private static final String API_KEY = "AIzaSyBJpw8UEzjjIOPLZ0g9nn8R0kOnKt2FWRg"; 
    private static final String CX = "b68c9cb5e25794900";

    public HashMap<String, String> query(String searchTerm) {
        HashMap<String, String> result = new HashMap<>();
        try {
            // 關鍵策略：自動加上電影相關關鍵字
            String query = searchTerm + " movie 影評 電影";
            String urlStr = "https://www.googleapis.com/customsearch/v1?key=" + API_KEY + "&cx=" + CX + "&q=" + URLEncoder.encode(query, "UTF-8") + "&num=10";

            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
            br.close();

            // 這裡為了簡化不引入 JSON Library，使用簡單字串處理提取 Title 和 Link
            // 實際專案建議使用 org.json
            String json = response.toString();
            int itemIndex = 0;
            while ((itemIndex = json.indexOf("\"link\": \"", itemIndex)) != -1) {
                int linkStart = itemIndex + 9;
                int linkEnd = json.indexOf("\"", linkStart);
                String link = json.substring(linkStart, linkEnd);

                int titleStartTag = json.lastIndexOf("\"title\": \"", linkStart);
                int titleStart = titleStartTag + 10;
                int titleEnd = json.indexOf("\"", titleStart);
                String title = json.substring(titleStart, titleEnd);

                result.put(title, link);
                itemIndex = linkEnd;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}