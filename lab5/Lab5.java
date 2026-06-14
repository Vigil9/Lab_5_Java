import java.util.*;

/**
 * Лабораторна робота №5
 * Вкладені класи: Каталог книжок з історією видачі читачам
 */
public class Lab5 {

    // =====================================================================
    // Інтерфейс для пошуку (використовується анонімними класами)
    // =====================================================================
    interface SearchCriteria<T> {
        boolean matches(T item);
    }

    // =====================================================================
    // Зовнішній клас — Каталог
    // =====================================================================
    static class Catalog {

        private String name;
        private List<Book> books = new ArrayList<>();

        public Catalog(String name) {
            this.name = name;
        }

        public void addBook(Book book) {
            books.add(book);
            System.out.println("[Каталог] Додано книгу: \"" + book.getTitle() + "\"");
        }

        public String getName() { return name; }
        public List<Book> getBooks() { return Collections.unmodifiableList(books); }

        // Пошук книг за критерієм (використовує інтерфейс SearchCriteria)
        public List<Book> searchBooks(SearchCriteria<Book> criteria) {
            List<Book> result = new ArrayList<>();
            for (Book b : books) {
                if (criteria.matches(b)) result.add(b);
            }
            return result;
        }

        // Пошук записів видачі по всьому каталогу
        public List<Book.IssueRecord> searchIssues(SearchCriteria<Book.IssueRecord> criteria) {
            List<Book.IssueRecord> result = new ArrayList<>();
            for (Book b : books) {
                result.addAll(b.searchIssues(criteria));
            }
            return result;
        }

        // =====================================================================
        // Внутрішній клас — Книжка (нестатичний: має доступ до полів Catalog)
        // =====================================================================
        class Book {

            private String title;
            private String author;
            private int year;
            private List<IssueRecord> issueHistory = new ArrayList<>();

            public Book(String title, String author, int year) {
                this.title = title;
                this.author = author;
                this.year = year;
            }

            public String getTitle()  { return title; }
            public String getAuthor() { return author; }
            public int getYear()      { return year; }
            public String getCatalogName() { return Catalog.this.name; } // доступ до зовнішнього класу

            public void issueToReader(String reader, String issueDate, String returnDate) {
                IssueRecord record = new IssueRecord(reader, issueDate, returnDate);
                issueHistory.add(record);
                System.out.println("[Видача] \"" + title + "\" -> " + reader
                        + " (" + issueDate + " – " + returnDate + ")");
            }

            public List<IssueRecord> getIssueHistory() {
                return Collections.unmodifiableList(issueHistory);
            }

            public List<IssueRecord> searchIssues(SearchCriteria<IssueRecord> criteria) {
                List<IssueRecord> result = new ArrayList<>();
                for (IssueRecord r : issueHistory) {
                    if (criteria.matches(r)) result.add(r);
                }
                return result;
            }

            public void printInfo() {
                System.out.println("  Книга  : \"" + title + "\"");
                System.out.println("  Автор  : " + author);
                System.out.println("  Рік    : " + year);
                System.out.println("  Каталог: " + getCatalogName());
                System.out.println("  Видач  : " + issueHistory.size());
            }

            // =================================================================
            // Статичний вкладений клас — Запис про видачу
            // (статичний: не потребує екземпляра Book)
            // =================================================================
            static class IssueRecord {
                private String readerName;
                private String issueDate;
                private String returnDate;

                public IssueRecord(String readerName, String issueDate, String returnDate) {
                    this.readerName = readerName;
                    this.issueDate  = issueDate;
                    this.returnDate = returnDate;
                }

                public String getReaderName() { return readerName; }
                public String getIssueDate()  { return issueDate; }
                public String getReturnDate() { return returnDate; }

                @Override
                public String toString() {
                    return readerName + " [" + issueDate + " – " + returnDate + "]";
                }
            }
        } // кінець Book
    } // кінець Catalog

    // =====================================================================
    // Локальний клас — статистика каталогу (оголошений всередині методу)
    // =====================================================================
    static void printCatalogStats(Catalog catalog) {

        // Локальний клас — існує лише в межах цього методу
        class CatalogStats {
            int totalBooks;
            int totalIssues;
            String mostPopular;

            CatalogStats(Catalog c) {
                totalBooks = c.getBooks().size();
                totalIssues = 0;
                int maxIssues = -1;
                mostPopular = "—";
                for (Catalog.Book b : c.getBooks()) {
                    int cnt = b.getIssueHistory().size();
                    totalIssues += cnt;
                    if (cnt > maxIssues) {
                        maxIssues = cnt;
                        mostPopular = b.getTitle();
                    }
                }
            }

            void print() {
                System.out.println("  Книг у каталозі : " + totalBooks);
                System.out.println("  Всього видач    : " + totalIssues);
                System.out.println("  Найпопулярніша  : \"" + mostPopular + "\"");
            }
        }

        System.out.println("\n=== Статистика каталогу «" + catalog.getName() + "» ===");
        new CatalogStats(catalog).print();
    }

    // =====================================================================
    // MAIN
    // =====================================================================
    public static void main(String[] args) {
        System.out.println("=== Лабораторна робота №5: Вкладені класи ===\n");

        // --- Створення каталогу та книжок ---
        Catalog catalog = new Catalog("Міська бібліотека");

        Catalog.Book b1 = catalog.new Book("Кобзар", "Тарас Шевченко", 1840);
        Catalog.Book b2 = catalog.new Book("Тіні забутих предків", "Михайло Коцюбинський", 1912);
        Catalog.Book b3 = catalog.new Book("Місто", "Валер'ян Підмогильний", 1928);
        Catalog.Book b4 = catalog.new Book("Захар Беркут", "Іван Франко", 1883);

        catalog.addBook(b1);
        catalog.addBook(b2);
        catalog.addBook(b3);
        catalog.addBook(b4);

        System.out.println();

        // --- Додавання записів про видачу ---
        System.out.println("--- Видача книжок читачам ---");
        b1.issueToReader("Олена Коваль",    "01.01.2025", "15.01.2025");
        b1.issueToReader("Іван Мороз",      "20.01.2025", "05.02.2025");
        b1.issueToReader("Марина Петренко", "10.03.2025", "25.03.2025");
        b2.issueToReader("Іван Мороз",      "05.02.2025", "20.02.2025");
        b2.issueToReader("Sofiya Bondar",   "01.03.2025", "14.03.2025");
        b3.issueToReader("Олена Коваль",    "16.01.2025", "30.01.2025");
        b3.issueToReader("Dmytro Savchenko","01.04.2025", "20.04.2025");
        b4.issueToReader("Марина Петренко", "26.03.2025", "10.04.2025");

        // --- Інформація про книги ---
        System.out.println("\n--- Інформація про книги ---");
        for (Catalog.Book b : catalog.getBooks()) {
            b.printInfo();
            System.out.println();
        }

        // --- Пошук за допомогою АНОНІМНИХ КЛАСІВ (реалізують інтерфейс SearchCriteria) ---
        System.out.println("--- Пошук 1: книги, видані після 1900 р. ---");
        List<Catalog.Book> found1 = catalog.searchBooks(new SearchCriteria<Catalog.Book>() {
            @Override
            public boolean matches(Catalog.Book book) {
                return book.getYear() > 1900;
            }
        });
        for (Catalog.Book b : found1)
            System.out.println("  Знайдено: \"" + b.getTitle() + "\" (" + b.getYear() + ")");

        System.out.println("\n--- Пошук 2: всі видачі читача «Іван Мороз» ---");
        List<Catalog.Book.IssueRecord> found2 = catalog.searchIssues(
            new SearchCriteria<Catalog.Book.IssueRecord>() {
                @Override
                public boolean matches(Catalog.Book.IssueRecord record) {
                    return record.getReaderName().equals("Іван Мороз");
                }
            }
        );
        for (Catalog.Book.IssueRecord r : found2)
            System.out.println("  Видача: " + r);

        System.out.println("\n--- Пошук 3: книги, які брали більше 2 разів ---");
        List<Catalog.Book> found3 = catalog.searchBooks(new SearchCriteria<Catalog.Book>() {
            @Override
            public boolean matches(Catalog.Book book) {
                return book.getIssueHistory().size() > 2;
            }
        });
        if (found3.isEmpty()) System.out.println("  (не знайдено)");
        for (Catalog.Book b : found3)
            System.out.println("  \"" + b.getTitle() + "\" — " + b.getIssueHistory().size() + " видачі");

        // --- Статистика через локальний клас ---
        printCatalogStats(catalog);

        System.out.println("\n=== Роботу завершено ===");
    }
}
