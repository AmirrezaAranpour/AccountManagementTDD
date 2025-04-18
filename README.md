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
اسکرین شات این بخش:
![photo_5769504052495895967_y](https://github.com/user-attachments/assets/a19d519e-5b97-411c-85e6-0025a1c6e113)


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

![photo_5769504052495895968_y](https://github.com/user-attachments/assets/8ceb425c-5c26-4e84-8595-002bc27da968)

 اکنون تست افزوده‌شده و تمامی تست‌های قبلی با موفقیت عبور می‌کنند.



## ۳. مشکلات نوشتن تست **بعد** از کد (پاسخ پرسش 3)

1. ممکن است  به‌طور ناخودآگاه، آزمون‌هایی بنویسیم که با رفتار فعلی کد مطابقت دارند، نه اینکه به‌درستی نیازمندی‌ها را ارزیابی کنند.همان طور که در ابتدا حتی به‌سختی می‌توانستیم اشکال را پیدا کنیم ، با وجود اینکه این موضوع در مستندات ذکر شده بود. 
2. بدون داشتن آزمون‌هایی که طراحی را از ابتدا هدایت کنند، ممکن است موارد خاص یا عملکردهای مهم را از قلم بیندازیم. در این حالت، آزمون‌ها به یک فکر ثانویه تبدیل می‌شوند، و یک اسپسیفیکیشن دقیق و هدفمند.
3. اگر از ابتدا به قابلیت آزمون‌پذیری فکر نکنیم، در نهایت به کدی می‌رسیم که آزمون‌نویسی برای آن سخت است واعمال اصولی مانند **چابک (Agile)، معماری مایکروسرویس، یا رضایت مشتری** ممکن است دشوار یا حتی غیرممکن شود.


# گزارش بخش دوّم – پیاده‌سازی تاریخچۀ تراکنش با رویکرد TDD 

---

## ۱. فعال‌سازی تست‌های آماده

- سه تست انتهایی در فایل `AccountBalanceCalculatorTest.java` از حالت کامنت خارج شد:  
  - `testTransactionHistoryAfterDeposits`  
  - `testTransactionHistoryAfterDepositsAndWithdrawals`  
  - `testTransactionHistoryShouldContainOnlyLastCalculationTransactions`  
- اجرای `mvn test` منجر‌به **fail** این سه تست گردید؛ سیستم قابلیت ذخیرهٔ تاریخچهٔ آخرین محاسبه را نداشت.

---

## ۲. پیاده‌سازی ویژگی (Green Phase)

### ۲‑۱. تغییر کد

فایل **`src/edu/sharif/selab/AccountBalanceCalculator.java`** به‌شرح زیر اصلاح شد:

```java
// تعریف قبلی
private static List<Transaction> transactionHistory = new ArrayList<>();

public static int calculateBalance(List<Transaction> transactions) {
    …
    /* پس از پایان حلقه و محاسبهٔ موجودی */
    transactionHistory = new ArrayList<>(transactions); // ← ذخیرۀ ایمن آخرین تراکنش‌ها
    return balance;
}
```

- با جایگزینی کل لیست، هر بار فقط **آخرین** مجموعه‌ تراکنش‌ها نگه‌داری می‌شود.
- متدهای `getTransactionHistory`, `clearTransactionHistory` بدون تغییر باقی ماندند.

### ۲‑۲. اجرای مجدد تست‌ها

```text
-------------------------------------------------------
 T E S T S
-------------------------------------------------------
Running edu.sharif.selab.AccountBalanceCalculatorTest
Tests run: 11, Failures: 0, Errors: 0, Skipped: 0
-------------------------------------------------------
BUILD SUCCESS
```

همۀ ۱۱ تست (شامل سه تست TDD) اکنون سبز هستند.

---
![photo_5769504052495895972_y](https://github.com/user-attachments/assets/31024174-d6d1-44ae-94f7-7840c2d11428)


## ۳. پاسخ پرسش چهار – مزیت تست قبل از کد

نوشتن تست‌ها **پیش از پیاده‌سازی**:

نوشتن آزمون‌ها قبل از پیاده‌سازی ما را مجبور می‌کند که پیش از نوشتن کد، دقیق‌تر و عمیق‌تر درباره‌ی نیازمندی‌ها فکر کنیم. این رویکرد یک چرخه‌ی بازخورد مؤثر ایجاد می‌کند که در آن، آزمون‌ها طراحی را هدایت می‌کنند، نه اینکه صرفاً در انتهای کار، تأییدی ناقص باشند.


---

## ۴. پاسخ پرسش پنج – مزایا و معایب TDD

| مزایا | معایب |
|-------|------|
| اعتماد بالا به صحت کد؛ پوشش شکست‌ها سریع است. | شروع کندتر؛ ابتدا باید سناریو بنویسیم. |
| طراحی ماژولار و قابل‌تست؛ وابستگی‌ها شفاف‌تر می‌شوند. | در پروژه‌های با نیازمندی‌های ناپایدار، بازنویسی تست‌ها هزینه‌زا است. |
| تست‌ها مستندات زندهٔ رفتار سیستم‌اند. | برای UI یا کدهای وابسته به فریم‌ورک، تست واحد گران و دشوار است. |

---
