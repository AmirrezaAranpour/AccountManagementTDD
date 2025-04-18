# گزارش بخش اوّل – عیب‌یابی، نوشتن تست و رفع باگ  

## ۱. کشف باگ


### باگ چه بود ؟ 
متد calculateBalance ورودی با مبالغ منفی را می‌پذیرد؛ نتیجهٔ آن معکوسِ انتظار است (سپردهٔ ‑100 موجودی را کم و برداشت ‑100 موجودی را زیاد می‌کند).

### چرا دیده نمیشد ؟ 
۷ تست موجود تنها با مقادیر مثبتِ پول سر و کار داشتند و هیچ مسیر ورودی نامعتبر را اجرا نمی‌کردند، بنابراین باگ پنهان ماند.


---

## ۲. نمایش باگ، نوشتن تست و رفع آن

### ۲‑۱. تست افزوده شده (ابتدا شکست می‌خورد)

```java
@Test
void testNegativeAmountShouldThrow() {
    List<Transaction> transactions = Collections.singletonList(
        new Transaction(TransactionType.DEPOSIT, -100)
    );
    assertThrows(IllegalArgumentException.class,
                 () -> AccountBalanceCalculator.calculateBalance(transactions),
                 "Negative amounts must be rejected");
}
```
اسکرین شات بخش

![alt text](photo_5769504052495895967_y-1.jpg)

### ۲‑۲. اصلاح متد `calculateBalance`

```java
public static int calculateBalance(List<Transaction> transactions) {
    if (transactions == null)
        throw new IllegalArgumentException("Transaction list cannot be null");

    int balance = 0;
    for (Transaction t : transactions) {
        /* --- مانع‌گذاری جدید --- */
        if (t.getAmount() < 0)
            throw new IllegalArgumentException("Transaction amount cannot be negative");
        /* ---------------------- */

        if (t.getType() == TransactionType.DEPOSIT)
            balance += t.getAmount();
        else   // WITHDRAWAL
            balance -= t.getAmount();
    }
    return balance;
}
```

### ۲‑۳. نتیجهٔ اجرای تست‌ پس از اصلاح

```text
-------------------------------------------------------
 T E S T S
-------------------------------------------------------
Running edu.sharif.selab.AccountBalanceCalculatorTest
Tests run: 8, Failures: 0, Errors: 0, Skipped: 0
-------------------------------------------------------
BUILD SUCCESS
```

![alt text](photo_5769504052495895968_y-1.jpg)
 اکنون تست افزوده‌شده و تمامی تست‌های قبلی با موفقیت عبور می‌کنند.



## ۳. مشکلات نوشتن تست **بعد** از کد (پاسخ پرسش 3)

1. تست‌های پسینی معمولاً رفتار موجود را «مُهر تأیید» می‌زنند نه رفتار مطلوب را؛ لذا مسیرهای خطا یا ورودی نامعتبر پوشش نمی‌یابد.  
2. برنامه‌نویس ناخواسته از نوشتن تست‌هایی که نیاز به بازآرایی دارند طفره می‌رود؛ خطاها پنهان می‌مانند.  
3. نبودِ بازخورد لحظه‌ای، هزینهٔ اصلاح و ریسک تغییر را بالاتر می‌برد.

