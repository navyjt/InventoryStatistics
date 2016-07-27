package util;

import java.awt.Font;

import javax.swing.UIManager;

public class UI {

	public static void start() {
		// TODO 自动生成的方法存根
		
		Font font = new Font("微软雅黑",Font.PLAIN,13);
		
		//确保程序的UI显示一致
		UIManager.put("ToolTip.font",font);
		UIManager.put("Table.font",font);
		UIManager.put("TableHeader.font",font); 
		UIManager.put("TextField.font",font); 
		UIManager.put("ComboBox.font",font); 
		UIManager.put("TextField.font",font); 
		UIManager.put("PasswordField.font",font); 
		UIManager.put("TextArea.font",font); 
		UIManager.put("TextPane.font",font); 
		UIManager.put("EditorPane.font",font); 
		UIManager.put("FormattedTextField.font",font); 
		UIManager.put("Button.font",font); 
		UIManager.put("CheckBox.font",font); 
		UIManager.put("RadioButton.font",font); 
		UIManager.put("ToggleButton.font",font); 
		UIManager.put("ProgressBar.font",font); 
		UIManager.put("DesktopIcon.font",font); 
		UIManager.put("TitledBorder.font",font); 
		UIManager.put("Label.font",font); 
		UIManager.put("List.font",font); 
		UIManager.put("TabbedPane.font",font); 
		UIManager.put("MenuBar.font",font); 
		UIManager.put("Menu.font",font); 
		UIManager.put("MenuItem.font",font); 
		UIManager.put("PopupMenu.font",font); 
		UIManager.put("CheckBoxMenuItem.font",font); 
		UIManager.put("RadioButtonMenuItem.font",font); 
		UIManager.put("Spinner.font",font); 
		UIManager.put("Tree.font",font); 
		UIManager.put("ToolBar.font",font); 
		UIManager.put("OptionPane.messageFont",font); 
		UIManager.put("OptionPane.buttonFont",font);
    
	}

}
