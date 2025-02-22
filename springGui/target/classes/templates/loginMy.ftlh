<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link href="https://fonts.googleapis.com/css2?family=Roboto&display=swap" rel="stylesheet">
        <link rel="stylesheet" type="text/css" href="/styles/styleLogin.css">
    </head>
    <body>
        <div class="main">
            <h2 class="blue">Scheduling</h2><br>
            <form action="/login" method="post">
                <label for="username">Username</label>
                <input class="input-max" type="text" id="username" name="username" placeholder="Username" required>

                <div class="space"></div>
                <label for="password">Password</label>
                <input class="input-max" type="password" id="password" name="password" placeholder="Password" required>

                <div class="space"></div><br>
                <div class="remember">
                    <label>
                      <input type="checkbox" name="remember" value="remember"> Запомнить
                    </label>
                </div>

                <div class="space"></div><br>
                <div class="space"></div>
                <div class="error">Ошибка! Неверный логин или пароль</div>

                <div class="space"></div>
                <button type="submit" class="login-button" onclick="window.location.href='/courses'">
                    <h4>Login</h4>
                </button>

                <div class="space"></div>
                <button class="reg-button" onclick="window.location.href='/courses'">
                    <h4>Registration</h4>
                </button>
            </form>
        </div>
    </body>
</html>