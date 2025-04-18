# AccountManagementTDD
# مدیریت حساب بانکی

---

## ۱. بخش اول - کشف خطا

این پروژه شامل سه کلاس اصلی است:

| کلاس / فایل | مسئولیت |
|-------------|---------|
| `Transaction` | نگه‌داری نوع و مبلغ هر تراکنش |
| `TransactionType` | تعریف دو نوع تراکنش (`DEPOSIT` و `WITHDRAWAL`) |
| `AccountBalanceCalculator` | محاسبهٔ مانده‌حساب و نگه‌داری تاریخچهٔ آخرین محاسبه |

در ابتدای کار، هفت تست JUnit ۵ در `AccountBalanceCalculatorTest.java` وجود داشتند که همگی سبز بودند اما یکی از نیازمندی‌های مسئله را پوشش نمی‌دادند.

---

## ۲. اشکال یافت‌شده

| توضیح اشکال | دلیل دیده‌نشدن |
|-------------|----------------|
| پس از اجرای `calculateBalance`، لیست ایستا (static) `transactionHistory` به‌روزرسانی نمی‌شد؛ لذا متد `getTransactionHistory()` همیشه فهرست خالی برمی‌گرداند. | تمامی تست‌های موجود صرفاً قبل از محاسبه، خالی‌بودن تاریخچه را بررسی می‌کردند. دو تستی که قرار بود وضعیت تاریخچه بعد از محاسبه را وارسی کنند کامنت شده بودند؛ بنابراین پوشش تستی برای این نیازمندی وجود نداشت. |

---

## ۳. تست افزوده‌شده (Failing Test)

```java
@Test
void تاریخچه_بعد_از_محاسبه() {
    List<Transaction> txs = Arrays.asList(
        new Transaction(TransactionType.DEPOSIT, 120),
        new Transaction(TransactionType.WITHDRAWAL, 20)
    );

    AccountBalanceCalculator.calculateBalance(txs);

    List<Transaction> history = AccountBalanceCalculator.getTransactionHistory();
    assertEquals(txs.size(), history.size());
    assertTrue(history.containsAll(txs));
}
```

*اجرای این تست روی نسخهٔ قبلی باعث شکست (RED) می‌شود و اشکال را آشکار می‌کند.*

---

## ۴. رفع اشکال (Patch)

در کلاس `AccountBalanceCalculator`، متد `calculateBalance` به‌صورت زیر اصلاح شد:

```java
public static int calculateBalance(List<Transaction> transactions) {
    int balance = 0;
    transactionHistory.clear();        // پاک‌سازی تاریخچهٔ قبلی

    for (Transaction t : transactions) {
        switch (t.getType()) {
            case DEPOSIT    -> balance += t.getAmount();
            case WITHDRAWAL -> balance -= t.getAmount();
        }
        transactionHistory.add(t);     // ثبت تراکنش جاری
    }
    return balance;
}
```

تغییرات اصلی:
1. پاک‌کردن تاریخچهٔ قبلی پیش از هر محاسبه برای اطمینان از ثبت فقط آخرین محاسبات.
2. افزودن هر تراکنش به `transactionHistory` در همان حلقهٔ پردازش.

---

## ۵. نتیجهٔ اجرای تست‌ها

| وضعیت پیش از اصلاح | وضعیت پس از اصلاح |
|--------------------|-------------------|
| ۷ ✅ + ۱ ❌ *(تست جدید)* | ۸ ✅ *(همهٔ تست‌ها سبز)* |

---

## ۶. درس‌های آموخته‌شده دربارهٔ نوشتن تست پس از کدنویسی

1. **پوشش ناقص نیازمندی‌ها** – وقتی تست‌ها بعد از پیاده‌سازی نوشته می‌شوند، احتمال نادیده‌گرفتن سناریوهای مهم (مثل نگه‌داری تاریخچه) زیاد است.
2. **سوگیری تأیید** – توسعه‌دهنده ناخودآگاه تست‌هایی می‌نویسد که ثابت کند «کد فعلی» درست است، نه این‌که «رفتار موردنیاز» را تضمین کند.
3. **کاهش تست‌پذیری کد** – اگر ماژول‌‎ها از ابتدا با رویکرد «قابلیت تست» طراحی نشوند، افزودن تست‌های کامل در آینده دشوار و پرهزینه خواهد شد.

---
