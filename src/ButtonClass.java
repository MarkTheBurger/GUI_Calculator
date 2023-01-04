import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.UIManager;

public class ButtonClass extends JButton
{	
	private static final Font smallFont = new Font("Verdana", Font.BOLD, 24);
	private static final Color numbersBgColor = new Color(48, 50, 52);
	private static final Color specialBgColor = new Color(175, 143, 233);
	
	private String text;
	private float alphaValue;
	private Clip sound;

	public void setOpacity(float opacity) 
	{
		this.setBackground(
				new Color(
				(float)this.getBackground().getRed() / 255, 
				(float)this.getBackground().getGreen() / 255, 
				(float)this.getBackground().getBlue() / 255,
				opacity));
		
		CalculatorClass.buttonPanel.repaint();
	}
	 
	ButtonClass(String name)
	{
		this.text = name;
		this.setText(text);
		this.setFont(ButtonClass.smallFont);
		UIManager.getDefaults().put("Button.disabledText",Color.WHITE);
		this.setEnabled(false);
		
		// assigns a sound effect to the button
		String soundFile = Intro.getFilePath() + this.createSoundFile() + ".wav";
		
		try
		{
			sound = AudioSystem.getClip();
			AudioInputStream ais = AudioSystem.getAudioInputStream(new File(soundFile));
			sound.open(ais);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.out.println(getClass() + ": Failed to load a soundfile at the adress of " + soundFile);
		}
		
		// Colors the buttons differently depending on them being a number or an operation-related (+ - * /) button
		try
		{
			Integer.parseInt(text);
			
			this.setBackground(ButtonClass.numbersBgColor);
			alphaValue = 0.4f;
		}
		catch(Exception e)
		{
			if(text.equals("=") || text.equals("mode") || text.equals("deg") || text.equals("2nd"))
			{
				this.setBackground(new Color(255, 140, 0));
				alphaValue = 0.8f;
			}
			else
			{
				this.setBackground(ButtonClass.specialBgColor);
				alphaValue = 0.3f;
			}
		}
		
		this.setOpacity(alphaValue);
		this.setBorder(BorderFactory.createLoweredBevelBorder());
		
		this.addMouseListener(new MouseAdapter() 
		{
			// A hover effect over a button in the calculator
		    public void mouseEntered(MouseEvent evt) 
		    {
		    	setBorder(BorderFactory.createLineBorder(Color.WHITE));
		    	setOpacity(alphaValue + 0.2f);
		    }

		    public void mouseExited(MouseEvent evt) 
		    {
		    	setBorder(BorderFactory.createLoweredBevelBorder());
		    	setOpacity(alphaValue);
		    }
		    
		    @Override
            public void mousePressed(MouseEvent arg0) {
                if (arg0.getButton() == MouseEvent.BUTTON1)
                {
            		CalculatorClass.updateForButtons(text);
            		CalculatorClass.buttonPanel.repaint();
                } 
                else if (arg0.getButton() == MouseEvent.BUTTON3 && Intro.getAccessibilityMode()) // plays a sound effect after the user right-clicks a button in accessibility mode
                {
            		sound.start(); // play the sound
			       	sound.setFramePosition(0); // rewind to the beginning
                } 
            }
		});
	}
	
	public JComponent getButton()
	{
		return this;
	}
	
	// returns a soundFile specific to the button's text
		private String createSoundFile()
		{	
			switch(text)
			{
				case "1":
					return "one";
					
				case "2":
					return "two";
					
				case "3":
					return "three";
					
				case "4":
					return "four";
					
				case "5":
					return "five";
					
				case "6":
					return "six";
				
				case "7":
					return "seven";
					
				case "8":
					return "eight";
					
				case "9":
					return "nine";
					
				case "0":
					return "zero";
					
				case ".":
					return "point";
					
				case "C":
					return "clear";
					
				case "DEL":
					return "delete";
					
				case "EXP":
					return "exp";
					
				case "e":
					return "e_small";
					
				case "x^y":
					return "power";
					
				case "√":
					return "sqrt";
					
				case "mode":
					return "mode";
					
				case "(":
					return "open_bracket";
				
				case ")":
					return "closed_bracket";
					
				case "sin":
					return "sin";
					
				case "cos":
					return "cos";
					
				case "tan":
					return "tan";
					
				case "x!":
					return "factorial";
					
				case "|x|":
					return "abs";
					
				case "ln(x)":
					return "lnx";
					
				case "lg(x)":
					return "logx";
					
				case "π":
					return "pi";
					
				case "deg":
					return "degrees";
					
				case "rad":
					return "radians";
					
				case "2nd":
					return "2nd";
					
				case "x":
					return "multiply";
					
				case "÷":
					return "divide";
				
				case "+":
					return "plus";
					
				case "-":
					return "minus";
					
				case "=":
					return "equals";
					
				default:
					return "zero";
			}
		}
}