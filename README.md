# 📊 FinanceApp - Личный Финансовый Трекер

FinanceApp - веб-приложение для управления финансами с современным интерфейсом.  
Построено на **Spring Boot 3.5.5** с **Hibernate ORM** и **PostgreSQL**, использует **Thymeleaf + CSS** для фронтенда.

## 🌟 Основные Возможности

*   **Аутентификация и авторизация пользователей** (JWT).
*   **Добавление, редактирование и удаление** записей о доходах и расходах.
*   **Категоризация** транзакций (например, Зарплата, Продукты, Развлечения).
*   **Просмотр сводной статистики** (общий баланс, суммы по категориям).
*   **Интуитивный пользовательский интерфейс** на основе Thymeleaf.

## 🧰 Технологии

*   **Backend:** Java 21+, Spring Boot 3.5.5, Spring Security 6.5.3, Spring Data JPA, Spring MVC.
*   **База данных:** PostgreSQL
*   **Frontend:** Thymeleaf 3.1.3, HTML, CSS.
*   **Сборка:** Apache Maven 3.8+.
*   **Безопасность:** JWT для веб-форм, BCrypt для хэширования паролей.

## 📋 Требования

Перед началом убедитесь, что у вас установлено:

*   **Java Development Kit (JDK) 21** или выше.
*   **Apache Maven 3.8** или выше.
*   **Система управления базами данных** PostgreSQL

## 🔧 Установка и Запуск

### 1. Клонирование репозитория

## ⚙️ Установка и запуск

### 1) Клонирование
```bash
git clone https://github.com/Retretu/finance-app.git
cd finance-app
```

### 2) Конфигурация базы данных
Создайте базу данных PostgreSQL:
```sql
CREATE DATABASE <your_name>;
```
Затем создайте необходимые таблицы:
```sql
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    name VARCHAR(40) NOT NULL,
    email VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    role VARCHAR(30) NOT NULL
);

CREATE TABLE income_records (
    id SERIAL PRIMARY KEY,
    category VARCHAR(35) NOT NULL,
    amount DOUBLE PRECISION NOT NULL,
    date DATE NOT NULL,
    description VARCHAR(100),
    user_id INTEGER NOT NULL,
    
    CONSTRAINT fk_income_user
        FOREIGN KEY (user_id)
            REFERENCES users(id)
            ON DELETE CASCADE
);

CREATE TABLE expense_records (
    id SERIAL PRIMARY KEY,
    category VARCHAR(35) NOT NULL,
    amount DOUBLE PRECISION NOT NULL,
    date DATE NOT NULL,
    description VARCHAR(100),
    user_id INTEGER NOT NULL,

    CONSTRAINT fk_expense_user
        FOREIGN KEY (user_id)
            REFERENCES users(id)
            ON DELETE CASCADE
);
```

### 3) Настройте`src/main/resources/application.properties`:
```properties
# DB
spring.datasource.url=your_url
spring.datasource.username=your_username
spring.datasource.password=your_password

#JWT
jwt.secret=your_secret
#format: PT24H
#Example:30m - 30 minutes real life
jwt.lifetime=your_lifetime_for_token
```

### 4) Сборка и запуск
```bash
# Сборка проекта
mvn clean package

# Запуск приложения
mvn spring-boot:run
```

---

## 📡 Функциональность (Web Interface)

### Главная страница (`/`)
- **Знакомство**: приложение приветствует пользователя. После нажатия на кнопку совершает переход на страницу авторизации

### Авторизация

#### Login (`/login`)
- **Авторизация**: для входа в приложение требуется ввести почту и пароль. Если у пользователя нет аккаунта, то он может зарегистрировать новый, нажав *Sign up*

#### Registration (`/registration`)
- **Регистрация**: при регистрации пользователю необходимо ввести имя, почту для авторизации и пароль к своему аккаунту. После нажатия *Sign up* профиль будет добавлен в Базу данных

### Функционал для авторизованного пользователя

#### Главная страница (`/account`)
- **Верхний интерфейс**: сверху показывается общий доход и расход за все операции
- **Общая статистика**: показывается общий баланс, доходы и расходы за месяц, а также количество всех операций
- **Быстрые действия**: можно добавить транзакции доходов (`/account/income/add`) и расходов (`/account/expense/add`)
- **Редактирование и удаление всех транзакций**: В списке транзакций любую запись можно удалить и отредактировать

#### Страница с доходами (`/account/income`)
- **Верхний интерфейс**:общая сумма доходов за всё время и сумма доходов за настоящий месяц 
- **Основная статистика**: общая сумма доходов, средняя сумма в месяц и количество транзакций по выбранной категории
- **Добавление новой записи**: переход на страницу по добавлению новой записи (`/account/income/add`)
- **Список всех доходов**: возможность удаления и редактирования записей из списка
- **Функция сортировки по категориям**: пользователь сам сможет выбрать, по какой категории захочет увидеть статистику

#### Страница с расходами (`/account/expense`)
- **Верхний интерфейс**:общая сумма расходов за всё время и сумма расходов за настоящий месяц
- **Основная статистика**: общая сумма расходов, средняя сумма в месяц и количество транзакций по выбранной категории
- **Добавление новой записи**: переход на страницу по добавлению новой записи (`/account/expense/add`)
- **Список всех расходов**: возможность удаления и редактирования записей из списка
- **Функция сортировки по категориям**: пользователь сам сможет выбрать, по какой категории захочет увидеть статистику

#### Страница с добавлением доходов (`/account/income/add`) или расходов (`/account/expense/add`)
- **Выбор параметров для записи**: категория(из зарезервированных), сумма, дата (по умолчанию ставится настоящая) и описание(опционально)

#### Выход из аккаунта (`/logout`)
- **Выход**: пользователь выходит из своего аккаунта, а JWT удаляется из cookie 

---

## 🗂️ Структура проекта
```text
finance-app/
├── src/
│   ├── main/
│   │   ├── java/org/gouenji/financeapp/
│   │   │   ├── config/
│   │   │   │   ├── filter/   
│   │   │   │   │   └── JwtAuthenticationFilter.java        # Фильтр для проверки JWT-токенов              
│   │   │   │   └── SecurityConfig.java                     # Конфигурация Spring Security
│   │   │   ├── controller/
│   │   │   │   ├── common/
│   │   │   │   │   ├── PublicAuthorizationController.java  # Контроллер для страниц авторизации
│   │   │   │   │   └── PublicHomeController.java           # Контроллер для главной страницы
│   │   │   │   ├── exception/
│   │   │   │   │   └── GlobalExceptionHandler.java         # Обработчик глобальных исключений
│   │   │   │   └── secured/
│   │   │   │       ├── PrivateAccountController.java       # Контроллер для защищённых страниц аккаунта
│   │   │   │       └── PrivateAdminController.java         # Контроллер для защищённых страниц администратора
│   │   │   ├── dto.records/
│   │   │   │   ├── ExpenseRecordsContainer.java            # DTO для передачи контейнера расходов
│   │   │   │   └── IncomeRecordsContainer.java             # DTO для передачи контейнера доходов
│   │   │   ├── entity/
│   │   │   │   ├── enums/
│   │   │   │   │   └── records/
│   │   │   │   │       ├── ExpenseCategory.java            # Enum категорий расходов
│   │   │   │   │       └── IncomeCategory.java             # Enum категорий доходов
│   │   │   │   ├── users/
│   │   │   │   │   └── UserRole.java                       # Enum ролей пользователей
│   │   │   │   ├── records/
│   │   │   │   │   ├── ExpenseRecord.java                  # Сущность расхода
│   │   │   │   │   ├── IncomeRecord.java                   # Сущность дохода
│   │   │   │   │   └── Record.java                         # Интерфейс базовой записи
│   │   │   │   └── User.java                               # Сущность пользователя
│   │   │   ├── repository/
│   │   │   │   ├── ExpenseRecordRepository.java            # Репозиторий для работы с расходами
│   │   │   │   ├── IncomeRecordRepository.java             # Репозиторий для работы с доходами
│   │   │   │   └── UserRepository.java                     # Репозиторий для работы с пользователями
│   │   │   ├── service/
│   │   │   │   └── records/
│   │   │   │       ├── ExpenseRecordService.java           # Сервис для бизнес-логики расходов
│   │   │   │       └── IncomeRecordService.java            # Сервис для бизнес-логики доходов
│   │   │   │   └── UserService.java                        # Сервис для бизнес-логики пользователей
│   │   │   ├── util/
│   │   │   │   └── JwtTokenUtil.java                       # Утилита для работы с JWT-токенами
│   │   │   └── FinanceAppApplication.java                  # Главный класс приложения
│   │   ├── resources/
│   │   │   ├── static/
│   │   │   │   └── css/
│   │   │   │       ├── private/
│   │   │   │       │   ├── account-page.css                # Стили для страницы аккаунта
│   │   │   │       │   ├── expense-add-page.css            # Стили для страницы добавления расхода
│   │   │   │       │   ├── expense-page.css                # Стили для страницы расходов
│   │   │   │       │   ├── income-add-page.css             # Стили для страницы добавления дохода
│   │   │   │       │   └── income-page.css                 # Стили для страницы доходов
│   │   │   │       └── public/
│   │   │   │           ├── authorization/
│   │   │   │           │   └── authorization-pages.css     # Стили для страниц авторизации
│   │   │   │           ├── error/
│   │   │   │           │   └── error-page.css              # Стили для страницы ошибок
│   │   │   │           └── home-page.css                   # Стили для главной страницы
│   │   │   ├── templates/
│   │   │   │   ├── public/
│   │   │   │   │   ├── authorization/
│   │   │   │   │   │   ├── login-page.html                 # Шаблон страницы входа
│   │   │   │   │   │   └── registration-page.html          # Шаблон страницы регистрации
│   │   │   │   │   └── error/
│   │   │   │   │       └── error-page.html                 # Шаблон страницы ошибок
│   │   │   │   └── private/
│   │   │   │       ├── account-page.html                   # Шаблон главной страницы аккаунта
│   │   │   │       ├── expense-add-page.html               # Шаблон страницы добавления расхода
│   │   │   │       ├── expense-page.html                   # Шаблон страницы расходов
│   │   │   │       ├── income-add-page.html                # Шаблон страницы добавления дохода
│   │   │   │       └── income-page.html                    # Шаблон страницы доходов
│   │   │   └── application.properties                      # Основные настройки приложения
│   │   └── webapp/                                         # Папка для веб-ресурсов (не используется в Spring Boot)
│   └── test/java/org/gouenji/financeapp/                   # Тесты
├── pom.xml                                                 # Maven конфигурация
└── README.md                                               # Основная документация проекта
```

---
