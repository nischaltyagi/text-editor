package application;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Formatter;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import java.awt.BorderLayout;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.UndoManager;

import java.awt.Color;

public class SimpleTextEditor {
	
	private JTextArea textArea; //text area for application
	private final String title = "Simple Text Editor"; //title of application
	private JFrame frame;
	private File openedFile; //file currently in text editor
	Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard(); //for copy pasting
	UndoManager undo = new UndoManager();//for undo redoo
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					
					SimpleTextEditor window = new SimpleTextEditor();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public SimpleTextEditor() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setTitle(title);
		frame.setBounds(100, 100, 626, 446);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout(0, 0));
		
		textArea = new JTextArea();
		textArea.setBackground(Color.WHITE);
		textArea.setForeground(Color.BLACK);
		textArea.setFont(new Font("Monospaced", Font.BOLD, 15));
		frame.getContentPane().add(textArea,BorderLayout.NORTH);
		
		//make text wrap around textArea
		textArea.setLineWrap(true);
	    textArea.setWrapStyleWord(true);
		
		JScrollPane scrollPane = new JScrollPane(textArea);
		frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		JMenuBar menuBar = new JMenuBar();
		frame.getContentPane().add(menuBar, BorderLayout.NORTH);
		
		JMenu file = new JMenu("File");
		file.setFont(new Font("Arial", Font.PLAIN, 15));
		menuBar.add(file);
		
		JMenuItem open = new JMenuItem("Open");
		open.setFont(new Font("Arial", Font.PLAIN, 14));
		file.add(open);
		//Event listener on open menu item
		open.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				open();
			}
		});		
		
		JMenuItem saveAs = new JMenuItem("Save As");
		saveAs.setFont(new Font("Arial", Font.PLAIN, 14));
		file.add(saveAs);

		saveAs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				create();
			}
		});
		
		JMenuItem save = new JMenuItem("Save");
		save.setFont(new Font("Arial", Font.PLAIN, 14));
		file.add(save);
		
		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				created();
			}
		});		
		
		JMenuItem close = new JMenuItem("Close");
		close.setFont(new Font("Arial", Font.PLAIN, 14));
		file.add(close);

		close.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				close();
			}
		});
		
		JMenu edit = new JMenu("Edit");
		edit.setFont(new Font("Arial", Font.PLAIN, 15));
		menuBar.add(edit);
		
		JMenuItem cut = new JMenuItem("Cut");
		cut.setFont(new Font("Arial", Font.PLAIN, 14));
		edit.add(cut);
		
		cut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				StringSelection contents = new StringSelection(textArea.getText());
				clipboard.setContents(contents,contents);
				textArea.setText("");
			}
		});
		
		JMenuItem copy = new JMenuItem("Copy");
		copy.setFont(new Font("Arial", Font.PLAIN, 14));
		edit.add(copy);
		
		copy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				StringSelection contents = new StringSelection(textArea.getText());
				clipboard.setContents(contents,contents);
			}
		});
		
		JMenuItem paste = new JMenuItem("Paste");
		paste.setFont(new Font("Arial", Font.PLAIN, 14));
		edit.add(paste);

		paste.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				textArea.paste();
			}
		});
		
		JMenuItem ud = new JMenuItem("Undo");
		ud.setFont(new Font("Arial", Font.PLAIN, 14));
		edit.add(ud);

		ud.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
	            try {
	                undo.undo();
	             } catch (CannotRedoException cre) {
	                cre.printStackTrace();
	             }
			}
		});
		
		JMenuItem redo = new JMenuItem("Redo");
		redo.setFont(new Font("Arial", Font.PLAIN, 14));
		edit.add(redo);
		
		redo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
		        try {
		            undo.redo();
		        } catch (CannotRedoException cre) {
		            cre.printStackTrace();
		        }
			}
		});
		
		JMenuItem clear = new JMenuItem("Clear");
		clear.setFont(new Font("Arial", Font.PLAIN, 14));
		edit.add(clear);
		
		clear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				textArea.setText("");
			}
		});
		
	}
	//for open menu item
	private void open() {
		try{
			JFileChooser chooser = new JFileChooser();
			chooser.setDialogTitle("Select a Text file to open");
			chooser.showOpenDialog(null);
			
			openedFile = chooser.getSelectedFile();
			if(openedFile == null || !openedFile.exists()) {
				JOptionPane.showMessageDialog(null,"Failed to Open File, File does not exist!","Error",JOptionPane.ERROR_MESSAGE);
				openedFile = null;
				return;
			}
			BufferedReader br = new BufferedReader(new FileReader(openedFile));
			String content = "";
			int r = 0;
			while((r = br.read()) != -1) {
				content += (char)r;
			}
			br.close();
			textArea.setText(content);
			frame.setTitle(title+" - "+openedFile.getName());
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	//for save as menu item
	private void create() {
		try{
			JFileChooser chooser = new JFileChooser();
			chooser.setDialogTitle("Save As");
			chooser.showSaveDialog(null);
			
			openedFile = chooser.getSelectedFile();
			created();			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	//for save menu item
	private void created() {
		try{
			if(openedFile == null) {
				JOptionPane.showMessageDialog(null,"Failed to Save File, No File is Selected!","Error",JOptionPane.ERROR_MESSAGE);
				return;
			}
			String contents = textArea.getText();
			Formatter form = new Formatter(openedFile);
			form.format("%s", contents);
			form.close();
			frame.setTitle(title+" - "+openedFile.getName());
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	//for close menu item
	private void close() {
		if(openedFile == null) {
			JOptionPane.showMessageDialog(null,"Failed to Close File,No file is selected","Error",JOptionPane.ERROR_MESSAGE);
			return;
		}		
		try{
			int  input = JOptionPane.showConfirmDialog(null,"Do you want save before closing","Wait!",JOptionPane.YES_NO_OPTION);
			if(input == JOptionPane.YES_OPTION) {
				created();
			}
			textArea.setText("");
			openedFile = null;
			frame.setTitle(title);			
		}catch(Exception e){
			e.printStackTrace();
		}
	}		
}
