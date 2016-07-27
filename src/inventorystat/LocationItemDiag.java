package inventorystat;

/**
 * @author Tony
 * @20160726
 */

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import util.ConfigFile;
import util.ResultSetTableModel;

public class LocationItemDiag extends JDialog {

	private static final long serialVersionUID = -4987279052138968748L;
	public JFrame f = new JFrame("增加存放仓库");
	private JPanel oldLocationPanel = new JPanel();
	private JPanel addLocationPanel = new JPanel();
	private ResultSetTableModel model;
	private ResultSet rs;
	private Connection conn;
	private Statement stmt;
	private JScrollPane resultscrollPane;

	private JButton addButton;
	private JTextField newsubjcetText;

	public LocationItemDiag() {

		f.setSize(500, 500);
		f.setVisible(true);
		f.setLocationRelativeTo(null);

		oldLocationPanel
				.setBorder(new TitledBorder(new EtchedBorder(), "存放仓库列表"));
		oldLocationPanel.setBounds(20, 15, 200, 420);

		addLocationPanel.setBorder(new TitledBorder(new EtchedBorder(),
				"增加存放仓库"));
		addLocationPanel.setBounds(270, 15, 200, 420);

		// 绘制添加新专业的按钮
		addButton = new JButton("增加");
		addButton.setPreferredSize(new Dimension(120, 25));
		newsubjcetText = new JTextField(15);
		addLocationPanel.add(newsubjcetText);
		addLocationPanel.add(addButton);

		f.add(oldLocationPanel);
		f.add(addLocationPanel);

		f.setLayout(null);
		f.setVisible(true);

		loadoldsubjcet();
		addButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent arg0) {

				final String strnewsubject = newsubjcetText.getText();
				if (strnewsubject.isEmpty()) {
					JOptionPane.showMessageDialog(null, "请输入需要添加的存放地点！", "警告",
							JOptionPane.INFORMATION_MESSAGE);
				}

				else {

					try {
						addnewlocationtodb(strnewsubject);
						newsubjcetText.setText(null);
						// 在点击按钮后强制刷新一次左侧列表
						loadoldsubjcet();
					} catch (final SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}
		});
	}

	protected void addnewlocationtodb(final String string) throws SQLException {

		try {
			// 获取数据库连接
			conn = ConfigFile.getConnection();
			// 创建Statement
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);

			final String query = "insert into location (location) values ('"
					+ string + "') ; ";

			// 查询用户选择的数据表
			stmt.execute(query);
			JOptionPane.showMessageDialog(null, "插入存放地点成功！", "信息",
					JOptionPane.INFORMATION_MESSAGE);

		}

		catch (final Exception e1) {
			e1.printStackTrace();
		} finally {
			conn.close();
			stmt.close();

		}

	}

	private void loadoldsubjcet() {
		try {
			// 获取数据库连接
			conn = ConfigFile.getConnection();
			// 创建Statement
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);
		}

		catch (final Exception e1) {
			e1.printStackTrace();
		}

		// 获取数据并填充至表格中
		try {
			// 如果装载JTable的JScrollPane不为空
			if (resultscrollPane != null) {
				// 从主窗口中删除表格
				oldLocationPanel.remove(resultscrollPane);
				// resultscrollPane = null;
			}

			final String query = "select location from location ; ";

			// 查询用户选择的数据表
			rs = stmt.executeQuery(query);
			// 使用查询到的ResultSet创建TableModel对象
			model = new ResultSetTableModel(rs, false);

			// 使用TableModel创建JTable，并将对应表格添加到窗口中
			final Object[] columnTitle = { "存放地点" };
			final JTable table = new JTable(model);
			for (int i = 0; i < table.getColumnCount(); i++) {
				table.getColumnModel().getColumn(i)
						.setHeaderValue(columnTitle[i]);
			}
			resultscrollPane = new JScrollPane(table);
			resultscrollPane.setPreferredSize(new Dimension(oldLocationPanel
					.getWidth() - 30, oldLocationPanel.getHeight() - 50));
			resultscrollPane
					.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

			oldLocationPanel.add(resultscrollPane, BorderLayout.CENTER);

			// f.validate();
			f.setVisible(true);
		} catch (final SQLException e3) {
			e3.printStackTrace();
		}

	}

}
