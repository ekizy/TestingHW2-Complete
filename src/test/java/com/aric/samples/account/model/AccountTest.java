package com.aric.samples.account.model;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class AccountTest {

	private static final double DELTA = 1e-15; // It will be used for double comparison
	private static double SAMPLE_BALANCE = 1230;
	private static long SAMPLE_ID = 12;
	private static long SAMPLE_TCKN = 125;
	private static String SAMPLE_FIRST_NAME = "Yusuf";
	private static String SAMPLE_LAST_NAME = "Ekiz";
	Account sampleAccount;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Before
	public void setup(){
		sampleAccount = new Account();
		sampleAccount.setBalance(SAMPLE_BALANCE);
		sampleAccount.setId(SAMPLE_ID);
		sampleAccount.setOwnerFirstName(SAMPLE_FIRST_NAME);
		sampleAccount.setOwnerLastName(SAMPLE_LAST_NAME);
		sampleAccount.setOwnerTckn(SAMPLE_TCKN);
	}

	@Test
	public void testSetOwnerTckn(){
		long newTCKN = 12345;
		sampleAccount.setOwnerTckn(newTCKN);
		assertEquals(newTCKN,sampleAccount.getOwnerTckn());//check values
	}

	@Test
	public void testGetOwnerTckn(){
		assertEquals(SAMPLE_TCKN,sampleAccount.getOwnerTckn());
	}

	@Test
	public void testSetId(){
		long newID = 12345678;
		sampleAccount.setId(newID);
		assertEquals(newID,sampleAccount.getId());//check values
	}

	@Test
	public void testGetId(){
		assertEquals(SAMPLE_ID,sampleAccount.getId());
	}

	@Test
	public void testSetBalance(){
		double newBalance = 123.5;
		sampleAccount.setBalance(newBalance);
		assertEquals(newBalance,sampleAccount.getBalance(),DELTA);//check values
	}

	@Test
	public void testGetBalance(){
		assertEquals(SAMPLE_BALANCE,sampleAccount.getBalance(),DELTA);
	}

	@Test
	public void testSetOwnerFirstName(){
		String newFirstName = "Ceyda";
		sampleAccount.setOwnerFirstName(newFirstName);
		assertEquals(newFirstName,sampleAccount.getOwnerFirstName()); //check values
	}

	@Test
	public void testGetOwnerFirstName(){
		assertEquals(SAMPLE_FIRST_NAME,sampleAccount.getOwnerFirstName());
	}

	@Test
	public void testSetOwnerLastName(){
		String newLastName = "Aladağ";
		sampleAccount.setOwnerLastName(newLastName);
		assertEquals(newLastName,sampleAccount.getOwnerLastName());//check values
	}

	@Test
	public void testGetOwnerLastName(){
		assertEquals(SAMPLE_LAST_NAME,sampleAccount.getOwnerLastName());
	}

	@Test
	public void depositSuccess(){
        int sampleAmount = 20;
        sampleAccount.deposit(sampleAmount);
        assertEquals(sampleAmount + SAMPLE_BALANCE,sampleAccount.getBalance(),DELTA);//check values
	}

	@Test
	public void depositFail(){
	    int illegalAmount = -15;
        thrown.expect(IllegalArgumentException.class); //Expect an exception,because amount is negative
        sampleAccount.deposit(illegalAmount);//check values
	}

	@Test
	public void withdrawSuccess(){
        int sampleAmount =  1000;
        sampleAccount.withdraw(sampleAmount);
        assertEquals(SAMPLE_BALANCE - sampleAmount,sampleAccount.getBalance(),DELTA);//check values
	}

	@Test
	public void withdrawFail(){
        int sampleAmount = 1400;
	    thrown.expect(IllegalArgumentException.class);//Expect an exception, because amount is bigger than balance
	    sampleAccount.withdraw(sampleAmount);
	}

}
