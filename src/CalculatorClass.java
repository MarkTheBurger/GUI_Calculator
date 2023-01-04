import java.awt.*;
import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Arrays;
 
public class CalculatorClass 
{
	private final Color bgColor = new Color(0.031f, 0.00784f, 0.14118f, 1.f);
	private final static Font titleFont = new Font("Verdana", Font.BOLD, 70);
	private final static Font smallFont = new Font("Verdana", Font.BOLD, 36);
	private static final String[][] standartModeButtons = {
			{"C", "DEL", "√", "÷"}, 
			{"7", "8", "9", "x"}, 
			{"4", "5", "6", "-"}, 
			{"1", "2", "3", "+"}, 
			{"mode", "0", ".", "="}};
	private static final String[][] expandedModeButtons = {
			{"2nd", "sin", "cos", "tan", "π"}, 
			{"deg", "ln(x)", "lg(x)", "(", ")"},
			{"EXP", "C", "DEL", "√", "÷"},
			{"x^y", "7", "8", "9", "x"}, 
			{"|x|", "4", "5", "6", "-"}, 
			{"x!", "1", "2", "3", "+"}, 
			{"mode", "e", "0", ".", "="}};
	private static final String[] operatorsPriority = {"√", "^", "/", "*", "-", "+"};
	private static ArrayList<Integer> operatorsIndexes;
	private static ArrayList<String> operatorsNames;
	
	private static JFrame f;
	private static JLabel outputField;
	private static JLabel inputField;
	public static JPanel buttonPanel;
	
	private static boolean accessibilityMode = Intro.getAccessibilityMode();
	private static boolean scientificMode = false;
	
	CalculatorClass()
	{
		EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
            	initializeFrame();
            }
		});
	}
	
	// Initializes the calculator frame
	void initializeFrame()
	{
		f = new JFrame("Calculator");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.getContentPane().setBackground(bgColor);
		
		// Dynamically scales the application depending on the screen resolution
		int height = (int)(Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 1.5);
		int width = (int)(height / 1.3);
		
		f.setPreferredSize(new Dimension(width, height));
		f.add(initializeComponents());
		f.addKeyListener(l);
		f.setFocusable(true);
		f.requestFocus();
        f.setFocusTraversalKeysEnabled(false);
		f.pack();
		f.setLocationRelativeTo(null);
		f.setResizable(false);
		
		if(accessibilityMode)
			f.setAlwaysOnTop(true);
		
		f.setVisible(true);
	}
	
	private static JScrollPane inputFieldScroll;
	private static JScrollPane outputFieldScroll;
	private Component initializeComponents()
	{
		JPanel mainPanel = new JPanel(new BorderLayout());
		JPanel topPanel = new JPanel(new GridLayout(2,1));
		buttonPanel = new JPanel(new GridLayout(5, 4));
		
		inputField = new JLabel("0", SwingConstants.RIGHT);
		inputField.setFont(titleFont);
		inputField.setOpaque(true);
		inputField.setBackground(bgColor);
		inputField.setForeground(Color.WHITE);
		
		outputField = new JLabel(" ", SwingConstants.RIGHT);
		outputField.setFont(smallFont);
		outputField.setOpaque(true);
		outputField.setBackground(bgColor);
		outputField.setForeground(Color.WHITE);
		
		inputFieldScroll = new JScrollPane(inputField, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		inputFieldScroll.setBorder(BorderFactory.createEmptyBorder());
		outputFieldScroll = new JScrollPane(outputField, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		outputFieldScroll.setBorder(BorderFactory.createEmptyBorder());
		
		topPanel.add(inputFieldScroll);
		topPanel.add(outputFieldScroll);
		
		// fills the buttonPanel with calculator buttons
		setCalculatorMode();
		
		buttonPanel.setBackground(bgColor);

		mainPanel.add(topPanel, BorderLayout.NORTH);
		mainPanel.add(buttonPanel, BorderLayout.CENTER);
		
		return mainPanel;
	}
	
	// Allows the user to enter keyboard inputs as calculator prompts
	KeyListener l = new KeyListener()
	{

		@Override
		public void keyTyped(KeyEvent e) 
		{
			// Invoked when a key is typed. Uses KeyChar, char output
			
		}

		@Override
		public void keyPressed(KeyEvent e) // Invoked when a physical key is pressed down. Uses KeyCode, int output
		{
			if(e.getKeyCode() == KeyEvent.VK_BACK_SPACE)
			{
				setAnswerMode(false);
				deleteCharacter();
			}	
			else if(e.getKeyCode() == KeyEvent.VK_ENTER)
				updateForButtons("=");
			else if(validInput(e.getKeyCode()))
			{
				setAnswerMode(false);
				addCharacter(Character.toString(e.getKeyChar()));
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {
			// called whenever a button is released
		}
	};
	
	static void deleteCharacter()
	{
		// deletes the last character entered by the user
		String adjustedText = inputField.getText().length() > 1 ? inputField.getText().substring(0, inputField.getText().length()-1) : "0";
		inputField.setText(adjustedText);
		updateOutputField(inputField.getText());
	}
	
	static void addCharacter(String text)
	{
		// Identifies the user's character as either being a number or an operator
		try
		{
			Integer.parseInt(text);
			
			// either replaces the initial 0 with a number or appends the number to the string
			String adjustedText = inputField.getText().equals("0") ? text : inputField.getText() + text;
			
			inputField.setText(adjustedText);
		}
		catch(Exception exception) // an operator had been entered
		{
			// If the input line is empty and a special function is entered
			if(inputField.getText().equals("0"))
			{
				switch(text)
				{
					case "+": 
					case "*":
					case "/":
					case ".":
					case "^":
						inputField.setText("0" + text);
					break;
					
					default:
						inputField.setText(text);
				}
			}
			else // when there are already numbers/operators entered in the input line of the calculator
			{
				char previousChar = inputField.getText().charAt(inputField.getText().length()-1);
				
				switch(text)
				{
					case "+": 
					case "-":
					case "*":
					case "/":
					case "^":
						
						try // checks to see if the last character entered was an operator or a number
						{
							Integer.parseInt(Character.toString(previousChar));
							
							inputField.setText(inputField.getText() + text);
						}
						catch(Exception e)
						{ 	// if the last character in the calculator string was an operator (with an exception of the closed bracket), a new operator replaces the previous one
							if(previousChar == ')' || previousChar == '!')
								inputField.setText(inputField.getText() + text);
							else if(previousChar == '(')
							{
								if(text.contentEquals("-"))
									inputField.setText(inputField.getText() + text);
								else
									inputField.setText(inputField.getText() + "0" + text);
							}	
							else 
								inputField.setText(inputField.getText().substring(0, inputField.getText().length()-1) + text);
						}
					break;
					
					case ")":
						try // checks to see if the last character entered was an operator or a number
						{
							if(previousChar != ')')
								Integer.parseInt(Character.toString(previousChar));
							
							inputField.setText(inputField.getText() + ")");
						}
						catch(Exception e) // if the last character entered was an operator [ex. (5+ ], end the bracket with a zero [ex. (5+0)]
						{
							inputField.setText(inputField.getText() + "0)");
						}
					break;
					
					case "(":
						try
						{
							Integer.parseInt(Character.toString(previousChar));
							inputField.setText(inputField.getText() + "*(");
						}
						catch (Exception e)
						{
							if(previousChar != ')')
								inputField.setText(inputField.getText() + "(");
							else
								inputField.setText(inputField.getText() + "*(");
						}
					break;
					
					case ".":
						try 
						{
							Integer.parseInt(Character.toString(previousChar));
							
							if(!inputField.getText().contains("."))
								inputField.setText(inputField.getText() + ".");
							else
							{
								for(int i = inputField.getText().length() - 1; i >= 0; i --)
								{
									if(inputField.getText().charAt(i) == '.')
										break;
									
									try 
									{
										Integer.parseInt(Character.toString(inputField.getText().charAt(i)));
									}
									catch(Exception e)
									{
										inputField.setText(inputField.getText() + ".");
										break;
									}
								}
							}
						}
						catch (Exception e)
						{
							if(previousChar != '.') // if the previous character is an operator, add (0.)
								inputField.setText(inputField.getText() + "0.");
						}
					break;
					
					default: // case for numbers
						if(previousChar != '(')
							inputField.setText(inputField.getText() + "*" + text);
						else
							inputField.setText(inputField.getText() + text);
				}
			}
		}
		updateOutputField(inputField.getText());
	}
	
	static void updateOutputField(String text)
	{
		if(text.contentEquals("0"))
		{
			outputField.setText(" ");
			return;
		}
		
		// Evaluate the answer
		// if there are any operators, calculate the result
		while(findOperators(text))
		{
			text = brackets(text);
			if(text.contentEquals("Error"))
			{
				outputField.setText("Error");
				return;
			}
		}

		if(text.indexOf('.') != -1 && text.indexOf('.') + 3 <= text.length()) 
		{
			if(text.contains("E"))
				text = text.substring(0, text.indexOf('.') + 5) + text.substring(text.indexOf('E')); // crops all the decimal places except the first four + leaves 'E'
			else	
				text = text.substring(0, text.indexOf('.') + 5); // crops all the decimal places except the first four
		}
		
		outputField.setText("= " + text);
	}
	
	static boolean findOperators(String text)
	{
		// Stores the locations of all the operators in the text and their symbols
		operatorsIndexes = new ArrayList<Integer>();
		operatorsNames = new ArrayList<String>();
		
		boolean hasOperators = false;
		
		if(text.length() == 0)
			return false;
		
		for(int i = 0; i < text.length(); i++)
		{
			try 
			{
				Integer.parseInt(Character.toString(text.charAt(i)));
			}
			catch (Exception e)
			{
				if(text.charAt(i) != '.' && text.charAt(i) != 'E' && text.charAt(i) != '-')
				{
					operatorsIndexes.add(i);
					operatorsNames.add(Character.toString(text.charAt(i)));
					hasOperators = true;
					continue;
				}
				
				if(text.charAt(i) == '-')
				{
					if(i != 0)
					{
						try
						{
							Integer.parseInt(Character.toString(text.charAt(i-1)));
							operatorsIndexes.add(i);
							operatorsNames.add(Character.toString(text.charAt(i)));
							hasOperators = true;
							continue;
						}
						catch(Exception e2)
						{
							// if the minus is behind another operator like a square root or in the beginning of the string, it does not count
						}
					}
				}
			}
		}
		return hasOperators;
	}
	
	// should be a recursive function
	static String brackets(String initialText)
	{
		String textInBrackets;
		
		findOperators(initialText);
		
		// tries to find the brackets in the input string
		boolean openBracket = operatorsNames.indexOf("(") == -1 ? false : true;
		boolean closedBracket = operatorsNames.indexOf(")") == -1 ? false : true;
		
		//if there are no brackets, calculate the whole string
		if(!openBracket && !closedBracket)
		{
			// picks the highest priority operator, scans through the list to find the numbers surrounding it
			for(String i : operatorsPriority)
			{
				if(operatorsIndexes.size() > 0)
					initialText = compute(initialText, i);
				else
					break;
				
				if(initialText.contentEquals("Error"))
					return "Error";
			}
			return initialText;
		}
		else if(openBracket && !closedBracket) // if only the closed bracket exists, the open bracket is implied to be at the beginning of the string
		{
			initialText = initialText +  ")";
			findOperators(initialText);
		}
		else if(!openBracket && closedBracket) // if only the open bracket exists, the closed bracket is implied to be at the end of the string
		{
			initialText = "(" + initialText;
			findOperators(initialText);
		}
		
		int openBracketIndex = operatorsIndexes.get(operatorsNames.indexOf("("));
		int closedBracketIndex = operatorsIndexes.get(operatorsNames.indexOf(")")); // sqrt(0-5
		
		// if the brackets are right next to each other, return nothing as text
		if(openBracketIndex + 1 == closedBracketIndex)
			textInBrackets = "0";
		else
			textInBrackets = initialText.substring(openBracketIndex + 1, closedBracketIndex);
		 
		// the 'brackets' function calls on itself to check for more brackets
		StringBuffer buf = new StringBuffer(initialText);
		String finalResult = brackets(textInBrackets);
		if(finalResult.contentEquals("Error"))
			return "Error";
		
		buf.replace(openBracketIndex, closedBracketIndex + 1, finalResult);
		return buf.toString();
	}
	
	// An enhanced switch statement is used, it is a preview feature in Java 13
	static String compute(String text, String operator)
	{
		while(true)
		{
			int arrIndex = operatorsNames.indexOf(operator);
			
			// checks that the operator is inside the string and is not the last character
			if(arrIndex != -1 && operatorsIndexes.get(arrIndex) < text.length()-1)
			{
				try {
					int start, end;
					double x = 0, y = 0;
					
					// if the operator is the first one, start from the beginning of the string`
					if(arrIndex == 0)
						start = 0;
					else // start with the last available operator
						start = operatorsIndexes.get(arrIndex - 1) + 1;
					
					// if the operator is the last one, end with the end of the string
					if(arrIndex + 1 == operatorsIndexes.size())
						end = text.length();
					else // end with the next available operator
						end = operatorsIndexes.get(arrIndex + 1);
	
					// when the operator is a square root, whatever number is in front of it doesn't matter
					if(!operator.contentEquals("√"))
						x = Double.parseDouble(text.substring(start, operatorsIndexes.get(arrIndex)));
					
					// take the number present after the operator as the second number
					y = Double.parseDouble(text.substring(operatorsIndexes.get(arrIndex) + 1, end));
					
					String result = "";
					switch (operator)
					{
						case "+":
							result = String.valueOf(x+y);
						break;
						
						case "-":
							result = String.valueOf(x-y);
						break;
							
						case "*":
							result = String.valueOf(x*y);
						break;
							
						case "/":
							if(y == 0) // cannot divide by 0
								return "Error";
							
							result = String.valueOf(x/y);
						break;
							
						case "^":
							result = String.valueOf(Math.pow(x,y));
						break;
						
						case "√":
							if(y >= 0) // cannot get a square root of a negative number
								result = String.valueOf(Math.sqrt(y));
							else
								return "Error";
						break;
						
						default:
							result = "Error";
					};
	
					// removes the standart ".0" associated with a double as a result
					if(result.endsWith(".0"))
						result = result.substring(0, result.length()-2);
					
					StringBuilder temp = new StringBuilder(text);
					temp.replace(start, end, result);
					text = temp.toString();
				
				}
				catch(Exception e)
				{
					return "Error";
				}
			}
			else if(arrIndex == -1) // if the operator is not in the string, exit the "compute"
				break;
			else
			{
				operatorsIndexes.remove(arrIndex);
				operatorsNames.remove(arrIndex);
				text = text.substring(0, text.length()-1);
				break;
			}
			
			// Revalidates the modified string
			findOperators(text);
		}
		return text;
	}
	
	static void updateForButtons(String text)
	{
		if(!text.contentEquals("="))
			setAnswerMode(false);
		
		switch(text)
		{
			case "=":
				// if the equal sign is pressed, enter the 'answer' mode
				setAnswerMode(true);
				
				if(accessibilityMode && !outputField.getText().contentEquals(" "))
					new Number_To_Text(outputField.getText().substring(2));
			break;
			
			case "DEL":
				deleteCharacter();
			break;
			
			case "C":
				inputField.setText("0");
				updateOutputField(inputField.getText());
			break;
			
			case "mode": // switches the calculator modes
				scientificMode = !scientificMode;
				setCalculatorMode();
			break;
				
			case "x":
				addCharacter("*");
			break;
			
			case "÷":
				addCharacter("/");
			break;	
			
			case "x^y":
				addCharacter("^");
			break;
			
			case "(":
			case ")":
			case "√":
			case ".":
			case "+":
			case "-":
				addCharacter(text);
			break;
			
			default:
				try 
				{  
				    Integer.parseInt(text);  
				    addCharacter(text);
				} 
				catch(NumberFormatException e)
				{   
					// this exception catches all the buttons the functionality of which is not yet supported by the calculator
				}  
				
			break;
		}
			
		f.requestFocus();
	}
	
	// makes the answer box bigger and the input box smaller when the 'equals' sign is pressed and vice versa
	static void setAnswerMode(boolean answerMode)
	{
		if(answerMode)
		{
			outputField.setFont(titleFont);
			inputField.setFont(smallFont);
		}
		else
		{
			outputField.setFont(smallFont);
			inputField.setFont(titleFont);
		}
	}
	
	// Function responsible for button render depending on the calculator mode currently set (normal or scientific)
		static void setCalculatorMode()
		{
			GridBagConstraints g = new GridBagConstraints();
			g.fill = GridBagConstraints.BOTH;
			
			String[][] copiedArray;
			buttonPanel.removeAll();
			
			// if the calculator is currently in standard mode
			if(!scientificMode)
			{
				buttonPanel.setLayout(new GridLayout(5, 4));
				copiedArray = Arrays.copyOf(standartModeButtons, standartModeButtons.length);
			}
			else // if the calculator is currently in scientific mode
			{
				buttonPanel.setLayout(new GridLayout(7, 5));
				copiedArray = Arrays.copyOf(expandedModeButtons, expandedModeButtons.length);
			}
			
			for(g.gridy = 0; g.gridy < copiedArray.length; g.gridy++)
				for(g.gridx = 0; g.gridx < copiedArray[0].length; g.gridx++)                                         
					buttonPanel.add(new ButtonClass(copiedArray[g.gridy][g.gridx]).getButton(), g);
			
			f.revalidate();
			f.repaint();
		}
		
	private final int[] codes = {45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 61, 96, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 109, 110, 111, 151, 521};
	// Checks if the user's input is within a valid range of characters
	boolean validInput(int code)
	{
		for(int i : codes)
			if(code == i)
				return true;
		
			return false;
	}
}