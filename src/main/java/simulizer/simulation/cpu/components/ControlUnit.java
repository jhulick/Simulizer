package simulizer.simulation.cpu.components;

import java.util.Observable;

import simulizer.simulation.data.representation.Word;

/**this class represents the control unit of the 
 * simulated processor
 * @author Charlie Street
 *
 */
public class ControlUnit extends Observable
{
	private Word temp;//used for temporary data storage and transport 
	private ALU ALU;//the ALU component of the simulation
	private LSUnit LSUnit;//the load store unit
	private RegisterBlock registers;//the general purpose registers
	private ProgramCounter programCounter;
	private InstructionRegister instructionRegister;
	
	/**method initialises all components the control unit is linked to in the cpu
	 * 
	 * @param ALU the arithmetic and logic unit in the CPU
	 * @param LSUnit the load store unit in the CPU
	 * @param registers the block of general purpose registers
	 * @param programCounter the program counter for the CPU
	 * @param instructionRegister the instructionRegister for the cpu
	 */
	public ControlUnit(ALU ALU, LSUnit LSUnit, RegisterBlock registers, ProgramCounter programCounter, InstructionRegister instructionRegister)
	{
		super();
		this.temp = new Word();
		this.ALU = ALU;
		this.LSUnit = LSUnit;
		this.registers = registers;
		this.programCounter = programCounter;
		this.instructionRegister = instructionRegister;
	}
	
	/**this method gets the temporary data being held in the control unit
	 * 
	 * @return the temporary transport holder
	 */
	public Word getData()
	{
		return this.temp;
	}
	
	/**sets the temporary data holder in the control unit,
	 * this will be set by the control unit or other components that can access it
	 * @param word the word to store
	 */
	public synchronized void setData(Word word)
	{
		this.temp = word;
	}
	
	/**this method sends the data stored in temp and sends it to the alu
	 * 
	 */
	public void sendALU()
	{
		this.ALU.setData(this.getData());	
		notifyObservers();
		setChanged();
	}
	
	/**this method receives the temp data in the ALU and sends it to temp
	 * 
	 */
	public void receiveALU()
	{
		this.setData(this.ALU.getData());
		notifyObservers();
		setChanged();
	}
	
	/**this method sends the data stored in temp and sends it to the LSUnit
	 * 
	 */
	public void sendLSUnit()
	{
		this.LSUnit.setData(this.getData());	
		notifyObservers();
		setChanged();
	}
	
	/**this method receives the temp data in the LSUnit and sends it to temp
	 * 
	 */
	public void receiveLSUnit()
	{
		this.setData(this.LSUnit.getData());
		notifyObservers();
		setChanged();
	}
	
	/**this method sends the data stored in temp and sends it to the PC
	 * 
	 */
	public void sendProgramCounter()
	{
		this.programCounter.setData(this.getData());	
		notifyObservers();
		setChanged();
	}
	
	/**this method receives the temp data in the PC and sends it to temp
	 * 
	 */
	public void receiveProgramCounter()
	{
		this.setData(this.programCounter.getData());
		notifyObservers();
		setChanged();
	}
	
	
	/**this method sends the data stored in temp and sends it to the IR
	 * 
	 */
	public void sendInstructionRegister()
	{
		this.instructionRegister.setData(this.getData());	
		notifyObservers();
		setChanged();
	}
	
	/**this method receives the temp data in the IR and sends it to temp
	 * 
	 */
	public void receiveInstructionRegister()
	{
		this.setData(this.instructionRegister.getData());
		notifyObservers();
		setChanged();
	}
	
	/**reads a value from one of the registers in the block of general purpose registers
	 * 
	 * @param index the index of the register to retrieve
	 * @return the word stored at that index
	 */
	public Word readFromRegister(int index)
	{
		notifyObservers();
		setChanged();
		return this.registers.getRegister(index).getData();
	}
	
	/**this method will write a word to a specified register in the set 
	 * of general purpose registers available
	 * @param index the index of the register to write to
	 * @param toStore the word to be stored in the specified register
	 */
	public synchronized void writeToRegister(int index, Word toStore)
	{
		this.registers.setRegister(index, toStore);
		notifyObservers();
		setChanged();
	}
}