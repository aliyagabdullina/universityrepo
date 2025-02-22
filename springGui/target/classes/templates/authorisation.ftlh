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

                <label for="role">Роль</label>
                <input class="input-max" type="text" id="role" name="role" placeholder="Выберите роль" required>
                <div class="space"></div>

                <label for="school">Школа</label>
                <input class="input-max" type="text" id="school" name="school" placeholder="Выберите школу" required>
                <div class="space"></div>

                <label for="username">Username</label>
                <input class="input-max" type="text" id="username" name="username" placeholder="Username" required>

                <div class="space"></div>
                <label for="password">Password</label>
                <input class="input-max" type="password" id="password" name="password" placeholder="Password" required>


                <div class="space"></div><br><div class="space"></div><br><div class="space"></div>
                <button type="submit" class="login-button" onclick="window.location.href='/courses'">
                    <h4>Registration</h4>
                </button>
            </form>
        </div>
    </body>
</html>
