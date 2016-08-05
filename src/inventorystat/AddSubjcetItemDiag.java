package inventorystat;

/***
 * @author Tony
 * @20160725 完成专业类别的添加模块，因为static关键字的问题，排查了一晚上
 * 该dialog不能设置为static，这样才能保证每次进入该对话框时，
 * 都会new一个新的空间出来，保证每次对话框内获取的数据库元素都得到及时的刷新
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
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import util.ConfigFile;
import util.ResultSetTableModel;
import util.WriteLog;
public class AddSubjcetItemDiag extends JDialog {

	private static final long serialVersionUID = 3531259789500243442L;
	public JFrame f = new JFrame("增加专业类别");
	private JPanel oldSubjectPanel = new JPanel();
	private JPanel addNewSubjectPanel = new JPanel();
	private ResultSetTableModel model;
	private ResultSet rs;
	private Connection conn;
	private Statement stmt;
	// 此处也不能用static 不然会造成两个scrollpane的错误
	private JScrollPane resultScrollPane;
	private JButton addButton;
	private JTextField newsubjcetText;

	public AddSubjcetItemDiag(/* JFrame f, String string */) {

		f.setSize(500, 500);
		f.setVisible(true);
		f.setLocationRelativeTo(null);

		oldSubjectPanel.setBorder(new TitledBorder(new EtchedBorder(), "专业类别列表"));
		oldSubjectPanel.setBounds(20, 15, 200, 420);

		addNewSubjectPanel.setBorder(new TitledBorder(new EtchedBorder(), "增加专业类别"));
		addNewSubjectPanel.setBounds(270, 15, 200, 420);

		// 绘制添加新专业的按钮
		addButton = new JButton("增加");
		addButton.setPreferredSize(new Dimension(120, 25));
		newsubjcetText = new JTextField(15);
		addNewSubjectPanel.add(newsubjcetText);
		addNewSubjectPanel.add(addButton);

		f.add(oldSubjectPanel);
		f.add(addNewSubjectPanel);
		f.setLayout(null);
		f.setVisible(true);

		loadoldsubjcet();

		addButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				String strnewsubject = newsubjcetText.getText();
				if (strnewsubject.isEmpty()) {
					JOptionPane.showMessageDialog(null, "请输入需要添加的专业类别！", "警告", JOptionPane.INFORMATION_MESSAGE);
				}

				else {

					try {
						addnewsubjcettodb(strnewsubject);
						newsubjcetText.setText(null);
						// 在点击按钮后强制刷新一次左侧列表
						loadoldsubjcet();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}
		});
	}

	protected void addnewsubjcettodb(String string) throws SQLException {

		try {
			// 获取数据库连接
			conn = ConfigFile.getConnection();
			// 创建Statement
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			String query = "insert into category (category) values ('" + string + "') ; ";
			// 查询用户选择的数据表
			stmt.execute(query);
			JOptionPane.showMessageDialog(null, "插入专业类别成功！", "信息", JOptionPane.INFORMATION_MESSAGE);
		}

		catch (Exception e1) {
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
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
		}

		catch (Exception e1) {
			e1.printStackTrace();
		}

		// 获取数据并填充至表格中
		try {
			// 如果装载JTable的JScrollPane不为空
			if (resultScrollPane != null) {
				// 从主窗口中删除表格
				oldSubjectPanel.remove(resultScrollPane);
				// resultscrollPane = null;
			}

			String query = "select category from category ; ";

			// 查询用户选择的数据表
			rs = stmt.executeQuery(query);
			// 使用查询到的ResultSet创建TableModel对象
			model = new ResultSetTableModel(rs, true);
			// 为TableModel添加监听器，监听用户的修改
			model.addTableModelListener(new TableModelListener() {

				public void tableChanged(TableModelEvent evt) {

					int row = evt.getFirstRow();
					int column = evt.getColumn();
					new WriteLog("修改的列:" + column + " ，修改的行:" + row + " ，修改后的值:" + model.getValueAt(row, column));
				}
			});
			// 使用TableModel创建JTable，并将对应表格添加到窗口中
			Object[] columnTitle = { "专业类别" };
			JTable table = new JTable(model);
			for (int i = 0; i < table.getColumnCount(); i++) {
				table.getColumnModel().getColumn(i).setHeaderValue(columnTitle[i]);
			}
			resultScrollPane = new JScrollPane(table);
			resultScrollPane.setPreferredSize(new Dimension(oldSubjectPanel.getWidth() - 30, oldSubjectPanel
					.getHeight() - 50));
			resultScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

			oldSubjectPanel.add(resultScrollPane, BorderLayout.CENTER);

			// f.validate();
			f.setVisible(true);
		} catch (SQLException e3) {
			e3.printStackTrace();
		}

	}

}
