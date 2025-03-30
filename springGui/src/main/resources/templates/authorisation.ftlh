<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link href="https://fonts.googleapis.com/css2?family=Roboto&display=swap" rel="stylesheet">
        <link rel="stylesheet" href="/styles/styleLogin2.css">
    </head>
    <body>
        <div class="main">
            <h2 class="blue">Scheduling</h2><br>
            <form action="/login" method="post">
                <label for="name">ФИО</label>
                <input class="input-max" type="text" id="name" name="name" placeholder="Введите ФИО" required>
                <div class="space"></div>

                <label for="role">Название университета</label>
                <input class="input-max" type="text" id="university" name="university" placeholder="Выберите университет" required>
                <div class="space"></div>

                <label for="school">Должность</label>
                <input class="input-max" type="text" id="role" name="role" placeholder="Выберите должность" required>
                <div class="space"></div>

                <label for="username">Факультет</label>
                <input class="input-max" type="text" id="facuily" name="facuily" placeholder="Выберите факультет" required>
                <div class="space"></div>

                <label for="username">Почта</label>
                <input class="input-max" type="text" id="email" name="email" placeholder="Введите почту" required>
                <div class="space"></div>

                <label for="password">Пароль</label>
                <input class="input-max" type="password" id="password" name="password" placeholder="Введите пароль" required>


                <div class="space"></div><br><div class="space"></div><br><div class="space"></div>
                <button type="submit" class="login-button" onclick="window.location.href='/courses'">
                    <h4>Registration</h4>
                </button>
            </form>
        </div>
    </body>
</html>
