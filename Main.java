import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;

public class Main {
    public static void main(String[] args) {
        // 建立主視窗
        JFrame frame = new JFrame("Moogle 🎬");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setBackground(new Color(253, 245, 230)); // 米色背景

        // 佈局
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(new Color(253, 245, 230));

        // 上方搜尋區
        JPanel searchPanel = new JPanel();
        searchPanel.setBackground(new Color(253, 245, 230));
        JLabel titleLabel = new JLabel("Moogle 🎬");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 32));
        titleLabel.setForeground(new Color(139, 69, 19)); // 棕色字體

        JTextField searchField = new JTextField(30);
        searchField.setFont(new Font("SansSerif", Font.PLAIN, 16));
        
        JButton searchButton = new JButton("搜尋");
        
        searchPanel.add(titleLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        // 中間結果區 (使用 ScrollPane)
        JPanel resultPanel = new JPanel();
        resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));
        resultPanel.setBackground(new Color(253, 245, 230));
        JScrollPane scrollPane = new JScrollPane(resultPanel);
        scrollPane.setBorder(null);

        // 事件監聽：按下搜尋按鈕
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String query = searchField.getText();
                if (query.trim().isEmpty()) return;

                resultPanel.removeAll(); // 清空舊結果
                resultPanel.add(new JLabel("搜尋並分析中...請稍候..."));
                resultPanel.revalidate();
                resultPanel.repaint();

                // 開啟新執行緒跑搜尋，避免介面卡死
                new Thread(() -> {
                    SearchSystemFacade facade = new SearchSystemFacade();
                    List<WebNode> results = facade.searchAndRank(query);

                    SwingUtilities.invokeLater(() -> {
                        resultPanel.removeAll();
                        if (results.isEmpty()) {
                            resultPanel.add(new JLabel("找不到相關結果，或 API 額度已用完。"));
                        } else {
                            for (WebNode node : results) {
                                addResultItem(resultPanel, node);
                            }
                        }
                        resultPanel.revalidate();
                        resultPanel.repaint();
                    });
                }).start();
            }
        });

        mainPanel.add(searchPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        frame.add(mainPanel);
        frame.setVisible(true);
    }

    // 輔助方法：新增單筆搜尋結果到介面上
    private static void addResultItem(JPanel panel, WebNode node) {
        JPanel itemPanel = new JPanel();
        itemPanel.setLayout(new BoxLayout(itemPanel, BoxLayout.Y_AXIS));
        itemPanel.setBackground(new Color(253, 245, 230));
        itemPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        itemPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // 標題 (超連結樣式)
        JLabel titleLabel = new JLabel("<html><u>" + node.webPage.title + "</u></html>");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setForeground(new Color(0, 0, 238)); // 藍色連結
        titleLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        // 點擊事件：開啟瀏覽器
        titleLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                try {
                    Desktop.getDesktop().browse(new URI(node.webPage.url));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        // 網址與分數
        JLabel urlLabel = new JLabel(node.webPage.url);
        urlLabel.setForeground(Color.GRAY);

        itemPanel.add(titleLabel);
        itemPanel.add(urlLabel);
        itemPanel.add(Box.createVerticalStrut(10)); // 間距

        panel.add(itemPanel);
    }
}