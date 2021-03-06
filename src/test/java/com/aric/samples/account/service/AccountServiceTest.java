package com.aric.samples.account.service;

import com.aric.samples.account.model.Account;
import com.aric.samples.account.repository.AccountRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class AccountServiceTest {


    private AccountService accountService = new AccountService();

    private static final double DELTA = 1e-15; // It will be used for double comparison
    private AccountRepository mockAccountRepository;
    private static double SAMPLE_AMOUNT = 125.0;

    public Account sampleAccount1;
    public Account sampleAccount2;
    public Account sampleAccount3;

    public List<Account> sampleList;
    public ObjectMapper mapper;


    @Before
    public void setup() throws Exception {

        mapper = new ObjectMapper();

        sampleAccount1 = new Account();
        sampleAccount1.setId(1);
        sampleAccount1.setBalance(1000);
        sampleAccount1.setOwnerFirstName("Yusuf");
        sampleAccount1.setOwnerLastName("Ekiz");
        sampleAccount1.setOwnerTckn(125);

        sampleAccount2 = new Account();
        sampleAccount2.setId(2);
        sampleAccount2.setBalance(2000);
        sampleAccount2.setOwnerFirstName("Ceyda");
        sampleAccount2.setOwnerLastName("Aladağ");
        sampleAccount2.setOwnerTckn(128);

        sampleAccount3 = new Account();
        sampleAccount2.setId(3);
        sampleAccount2.setBalance(3000);
        sampleAccount2.setOwnerFirstName("Serkan");
        sampleAccount2.setOwnerLastName("Bekir");
        sampleAccount2.setOwnerTckn(125);

        //Created mock accounts
        sampleList = new ArrayList<Account>();

        mockAccountRepository = Mockito.mock(AccountRepository.class);//Mocked repository object

        Field field = ReflectionUtils.findField(AccountService.class,"accountRepository");
        ReflectionUtils.makeAccessible(field);
        ReflectionUtils.setField(field,accountService,mockAccountRepository); //changed the repository field of service with Mocked repository


        //A list which has accounts only with TCKN = 125
        sampleList.add(sampleAccount1);
        sampleList.add(sampleAccount3);

        Mockito.when(mockAccountRepository.findByOwnerTckn(125)).thenReturn(sampleList); //Mock repository methods.
        Mockito.when(mockAccountRepository.findOne(new Long(1))).thenReturn(sampleAccount1);
        Mockito.when(mockAccountRepository.findOne(new Long(2))).thenReturn(sampleAccount2);
        Mockito.when(mockAccountRepository.findOne(new Long(3))).thenReturn(sampleAccount3);

        Mockito.when(mockAccountRepository.save(sampleAccount1)).thenReturn(sampleAccount1);
        Mockito.when(mockAccountRepository.save(sampleAccount2)).thenReturn(sampleAccount2);
        Mockito.when(mockAccountRepository.save(sampleAccount3)).thenReturn(sampleAccount3);





    }

    @Test
    public void testDeposit() throws Exception {

        Long accountID = sampleAccount1.getId(); //get account id
        double oldBalance = sampleAccount1.getBalance(); //get old balance for comparison

        Account newAccount = accountService.deposit(accountID,SAMPLE_AMOUNT); //deposit an action

        assertEquals(oldBalance + SAMPLE_AMOUNT,newAccount.getBalance(),DELTA); //compare new balance with old + amount


    }

    @Test
    public void testEft() {
        Long senderID = sampleAccount1.getId();
        Long receiverID = sampleAccount2.getId(); // get accounts' ids.

        double senderOldBalance = sampleAccount1.getBalance(); //get sender's old balance

        Account newAccount = accountService.eft(senderID,receiverID,SAMPLE_AMOUNT); // it will return sender with new value.

        assertEquals(senderOldBalance-SAMPLE_AMOUNT,newAccount.getBalance(),DELTA); //compare values
    }

    @Test
    public void testFindPersonsByTckn() throws JsonProcessingException {
        //Test that findPersonsByTckn returns true list or not.

        List<Account> accounts = accountService.findPersonsByTckn(125);

        String jsonStringOfresult = mapper.writeValueAsString(accounts);
        String jsonStringOfSampleList = mapper.writeValueAsString(sampleList);

        assertEquals(jsonStringOfresult,jsonStringOfSampleList);


    }

}
