package simulizer.simulation.cpu.components;

import simulizer.simulation.data.representation.BinaryConversions;
import simulizer.simulation.data.representation.Word;

/**this class simulates the Arithemtic and Logic Unit
 * it contains operations for add, shift, mult
 * div, subtract, xor, or and, not
 * it will also include the immediate operations when appropriate
 * @author Charlie Street
 *
 */
public class ALU {
	
	/**this method carries out exclusive or on two words
	 * 
	 * @param firstWord the first word being used
	 * @param secondWord the second word being used
	 * @return the exclusive or of these two words
	 */
	public Word xor(Word firstWord, Word secondWord)
	{
		String firstString = firstWord.getWord();
		String secondString = secondWord.getWord();
		String result = "";
		
		for(int i = 0; i < firstString.length(); i++)//looping through the strings
		{
			int firstChar = Integer.parseInt(firstString.charAt(i)+"");
			int secondChar = Integer.parseInt(secondString.charAt(i)+"");
			
			if(firstChar + secondChar == 1)//0,1 or 1,0
			{
				result += '1';
			}
			else
			{
				result += '0';
			}
		}
		
		return new Word(result);
	}
	
	/**carries out inclusive or on two words, pretty much the same as xor with a couple of changes
	 * 
	 * @param firstWord the first word being used
	 * @param secondWord the second word being used
	 * @return the bitwise or of these two words
	 */
	public Word or(Word firstWord, Word secondWord)
	{
		String firstString = firstWord.getWord();
		String secondString = secondWord.getWord();
		String result = "";
		
		for(int i = 0; i < firstString.length(); i++)//looping through the strings
		{
			int firstChar = Integer.parseInt(firstString.charAt(i)+"");
			int secondChar = Integer.parseInt(secondString.charAt(i)+"");
			
			if(firstChar + secondChar == 0)//0,0
			{
				result += '0';
			}
			else
			{
				result += '1';
			}
		}
		
		return new Word(result);
	}
	
	/**method carries out the and operation on two words
	 * very similar to or, xor with different conditions
	 * @param firstWord
	 * @param secondWord
	 * @return a word as the result of the and operation
	 */
	public Word and(Word firstWord, Word secondWord)
	{
		String firstString = firstWord.getWord();
		String secondString = secondWord.getWord();
		String result = "";
		
		for(int i = 0; i < firstString.length(); i++)//looping through the strings
		{
			int firstChar = Integer.parseInt(firstString.charAt(i)+"");
			int secondChar = Integer.parseInt(secondString.charAt(i)+"");
			
			if(firstChar + secondChar == 2)//1,1
			{
				result += '1';
			}
			else
			{
				result += '0';
			}
		}
		
		return new Word(result);
	}
	
	/**this method will negate the bit string of a word
	 * 
	 * @param toNegate the word to be negated
	 * @return the negated Word
	 */
	public Word not(Word toNegate)
	{
		String word = toNegate.getWord();
		String result = "";//where to store the negated string
		
		for(int i = 0; i < word.length(); i++)
		{
			if(word.charAt(i)=='1')//if 1 then 0
			{
				result += '0';
			}
			else if(word.charAt(i)=='0')//if 0 then 1
			{
				result += '1';
			}
		}
		
		return new Word(result);
	}
	
	/**this method will shift a word left , i.e get bigger
	 * 
	 * @param toShift the word to be shifted
	 * @param shiftNumber the amount to be shifted by
	 * @return the shifted word
	 */
	private Word shiftLeft(Word toShift, Word shiftNumber)
	{
		String toBeShifted = toShift.getWord();
		int shift = (int)BinaryConversions.getLongValue(shiftNumber.getWord());
		
		for(int i = 0; i < shift; i++)//shifting to the left
		{
			toBeShifted += '0';
		}
		
		toBeShifted = toBeShifted.substring(shift);//taking away the now uneccsary characters
		
		return new Word(toBeShifted);
	}
	
	/**this method will shift a word right, i.e get smaller
	 * @param toShift the word to be shifted
	 * @param shiftNumber a NEGATIVE shift number
	 * @return the shifted word
	 */
	private Word shiftRight(Word toShift, Word shiftNumber)
	{
		String toBeShifted = toShift.getWord();
		int shift = (int)BinaryConversions.getLongValue(shiftNumber.getWord()) * -1;//making positive 
		
		toBeShifted = toBeShifted.substring(0,toBeShifted.length()-shift);//carrying out the shift
		
		while(toBeShifted.length() < 32)//adding the padding
		{
			toBeShifted = '0' + toBeShifted;
		}
		
		return new Word(toBeShifted);
	}
	
	/**generic shift, what direction the shift is in depends on the sign of the shift number
	 * 
	 * @param toShift the word to be shifted
	 * @param shiftNumber the shift amount
	 * @return the word shifted
	 */
	public Word shift(Word toShift, Word shiftNumber)
	{
		if(BinaryConversions.getLongValue(shiftNumber.getWord()) < 0)//if negative shift right
		{
			return shiftRight(toShift,shiftNumber);
		}
		else
		{
			return shiftLeft(toShift,shiftNumber);
		}
	}
	
}
