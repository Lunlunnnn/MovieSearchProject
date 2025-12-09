import java.util.ArrayList;

public class WebNode {
    public WebNode parent;
    public ArrayList<WebNode> children;
    public WebPage webPage;
    public double nodeScore;

    public WebNode(WebPage webPage) {
        this.webPage = webPage;
        this.children = new ArrayList<>();
    }

    public void setNodeScore(ArrayList<Keyword> keywords) {
        // 1. 計算本頁分數
        this.nodeScore = this.webPage.calculateScore(keywords);

        // 2. 遞迴計算子節點分數 (簡單版：只加總，不遞迴太深以免卡住)
        for (WebNode child : children) {
            child.setNodeScore(keywords);
            this.nodeScore += child.nodeScore * 0.5; // 子網頁權重打折，避免灌水
        }
    }

    public void addChild(WebNode child) {
        this.children.add(child);
        child.parent = this;
    }
}