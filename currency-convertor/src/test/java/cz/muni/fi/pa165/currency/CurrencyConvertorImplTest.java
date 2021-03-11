package cz.muni.fi.pa165.currency;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Currency;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class CurrencyConvertorImplTest {

    private static Currency CZK = Currency.getInstance("CZK");
    private static Currency EUR = Currency.getInstance("EUR");

    @Mock
    private ExchangeRateTable exchangeRateTable;

    private CurrencyConvertor currencyConvertor;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        currencyConvertor = new CurrencyConvertorImpl(exchangeRateTable);
    }

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testConvert() throws ExternalServiceFailureException {
        when(exchangeRateTable.getExchangeRate(EUR, CZK))
                .thenReturn(new BigDecimal("26.41"));

        assertEquals(new BigDecimal("1.00"), currencyConvertor.convert(CZK, EUR, new BigDecimal("26.41")));
        assertEquals(new BigDecimal("26.41"), currencyConvertor.convert(EUR, CZK, new BigDecimal("1.00")));
        assertEquals(new BigDecimal("1.00"), currencyConvertor.convert(CZK, CZK, new BigDecimal("1.00")));
        assertEquals(new BigDecimal("1.01"), currencyConvertor.convert(EUR, CZK, new BigDecimal("26.6741")));
    }

    @Test
    public void testConvertWithNullSourceCurrency() {
        expectedException.expect(IllegalArgumentException.class);
        currencyConvertor.convert(null, CZK, BigDecimal.ONE);
    }

    @Test
    public void testConvertWithNullTargetCurrency() {
        expectedException.expect(IllegalArgumentException.class);
        currencyConvertor.convert(EUR, null, BigDecimal.ONE);
    }

    @Test
    public void testConvertWithNullSourceAmount() {
        expectedException.expect(IllegalArgumentException.class);
        currencyConvertor.convert(EUR, CZK, null);
    }

    @Test
    public void testConvertWithUnknownCurrency() throws ExternalServiceFailureException {
        when(exchangeRateTable.getExchangeRate(EUR, CZK))
                .thenReturn(null);
        expectedException.expect(UnknownExchangeRateException.class);
        currencyConvertor.convert(EUR, CZK, BigDecimal.ONE);

    }

    @Test
    public void testConvertWithExternalServiceFailure() throws ExternalServiceFailureException {
        when(exchangeRateTable.getExchangeRate(EUR, CZK))
                .thenThrow(UnknownExchangeRateException.class);
        expectedException.expect(UnknownExchangeRateException.class);
        currencyConvertor.convert(EUR, CZK, BigDecimal.ONE);
    }

}