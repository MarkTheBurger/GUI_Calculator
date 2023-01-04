import java.io.File;
import java.util.ArrayList;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

// Voices the words given by the Number_To_Text_Class

public class Text_To_Speech {

	private final static String[] allNums = {"zero","one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten",
			"eleven", "twelve", "thirteen", "fourteen", "fifteen", "sixteen", "seventeen", "eighteen", "nineteen", 
			"twenty", "thirty", "forty", "fifty", "sixty", "seventy", "eighty", "ninety",
			"hundred", "thousand", "million", "billion", "trillion", "point", "E", "negative"};
	private final static String[] soundLocations = createSounds();
	private static Clip clip;
	
	private static String[] createSounds()
	{
		ArrayList <String> sounds = new ArrayList<String>();
		
		for(int i = 0; i < allNums.length; i++)
			sounds.add(Intro.getFilePath() + allNums[i] + ".wav");
		
		return sounds.toArray(new String[sounds.size()]);
	}
	
	Text_To_Speech(String text)
	{
		readResult(text);
	}
	
	public void readResult(String text) {
		ArrayList <String> words = new ArrayList<String>();
		
		// finds all the words in the string and stores them in a list
		while(text.length() > 0)
		{
			words.add(text.substring(0, text.indexOf(' ')));
			text = text.substring(text.indexOf(' ') + 1, text.length());
		}
		
		Thread thread = new Thread() {
			@Override
    	    public void run(){
				for(String i : words)
				{
					for(int j = 0; j < allNums.length; j++)
					{
						if(i.contentEquals(allNums[j]))
						{
							try
							{
								clip = AudioSystem.getClip();
								clip.open(AudioSystem.getAudioInputStream(new File(soundLocations[j])));
							}
							catch (Exception e)
							{
								System.out.println(getClass() + ": Couldn't play audio!");
								e.printStackTrace();
							}
							
							clip.start(); // play the sound
							
							try {
								// gives a longer pause to 'E'
								if(allNums[j] == "E")
									Thread.sleep(3000);
								else
									Thread.sleep(700);
								
							} catch (InterruptedException e) {
								
							}
							break;
						}
					}
				}
			}
		};
		
		thread.start();
	}
}