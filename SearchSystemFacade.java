import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchSystemFacade {
    private GoogleQuery googleQuery;

    public SearchSystemFacade() {
        this.googleQuery = new GoogleQuery();
    }

    public List<WebNode> searchAndRank(String userQuery) {
        // 1. 準備關鍵字 (除了使用者的，還加入我們自己的領域關鍵字)
        ArrayList<Keyword> keywords = new ArrayList<>();
        // 使用者輸入的字，權重最高 (Proposal 邏輯：越前面權重越高)
        String[] terms = userQuery.split(" ");
        for (int i = 0; i < terms.length; i++) {
            keywords.add(new Keyword(terms[i], 3.0 - (i * 0.5))); // 權重遞減
        }
        // 領域關鍵字
        keywords.add(new Keyword("電影", 2.0));
        keywords.add(new Keyword("影評", 2.0));
        keywords.add(new Keyword("劇情", 1.5));

        // 2. 呼叫 Google API 獲取初步結果
        HashMap<String, String> googleResults = googleQuery.query(userQuery);
        List<WebNode> nodeList = new ArrayList<>();

        // 3. 建立 WebNode 並抓取內容 (這步會花時間，實際應用建議用 Thread)
        for (Map.Entry<String, String> entry : googleResults.entrySet()) {
            String title = entry.getKey();
            String url = entry.getValue();
            
            System.out.println("Analyzing: " + title); // Log
            WebPage page = new WebPage(url, title);
            WebNode node = new WebNode(page);
            
            // TODO: 若要更強，這裡可以解析 page.content 抓出子連結並 addChildren
            // node.addChild(new WebNode(subPage)); 

            node.setNodeScore(keywords); // 計算分數
            nodeList.add(node);
        }

        // 4. 重新排序 (Re-ranking)
        // 依照 nodeScore 由大到小排序
        Collections.sort(nodeList, new Comparator<WebNode>() {
            @Override
            public int compare(WebNode o1, WebNode o2) {
                return Double.compare(o2.nodeScore, o1.nodeScore);
            }
        });

        return nodeList;
    }
}