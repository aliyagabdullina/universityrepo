<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link href="https://fonts.googleapis.com/css2?family=Roboto&display=swap" rel="stylesheet">
        <link rel="stylesheet" href="/styles/styleDataPages.css">
    </head>
    <body>
            <selection>
                <div class="container">
                    <div class="menu">
                        <h1>Scheduling</h1>
                        <p class="university">UNIVERSITY</p>
                        <div class="menu-buttons-container">
                            <button class="menu-button icon" onclick="window.location.href='/account'">
                                <img src="/images/account.svg" alt="Аккаунт" width="25" height="25">
                                <h4>Аккаунт</h4>
                            </button>
                            <button class="menu-button icon" onclick="window.location.href='/schedule'">
                                <img src="/images/scheduling.svg" alt="Аккаунт" width="25" height="25">
                                <h4>Расписание</h4>
                            </button>
                            <button class="menu-button icon highlight" onclick="window.location.href='/places'">
                                <img src="/images/data.svg" alt="Аккаунт" width="25" height="25">
                                <h4>Данные</h4>
                            </button>
                            <button class="menu-button icon" onclick="window.location.href='/timeslot'">
                                <img src="/images/time.svg" alt="Аккаунт" width="25" height="25">
                                <h4>Сетка пар</h4>
                            </button>
                        </div>
                        <div class="menu-buttons-container-bottom">
                            <button class="menu-button icon" onclick="window.location.href='/settings'">
                                <img src="/images/setting.svg" alt="Аккаунт" width="25" height="25">
                                <h4>Настройки</h4>
                            </button>
                        </div>
                    </div>
                    <div></div>
                    <div class="body">
                        <div class="header-create icon-back">
                            <h2>Редактирование данных</h2>
                            <button class="button-none" type="button" onclick="window.history.back()">
                                <img src="/images/back.svg" alt="Аккаунт" width="25" height="25">
                            </button>
                        </div>
                        <div class="main">
                            <form action="/program/create" method="post">
                                <div class="input-block">
                                    <h5>Программа</h5> <div>${program.name}</div><br>
                                </div>
                                <div class="input-block">
                                    <h5>Сложность</h5> <div>${program.complexity}</div><br>
                                </div>
                              <div class="input-block">
                                    <h5>Кол-во часов</h5> <div>${program.hours}</div><br>
                                </div>
                                <div class="table-style table-program">
                                <table>
                                    <tr>
                                        <th>День недели</th>
                                        <th>Макс. кол-во уроков</th>
                                        <th>Макс. сложность</th>
                                    </tr>
                                    <tr>
                                      <td><h3>
                                        ПН
                                        </h3></td>
                                      <td><input class="input-slot" type="number" name="lessons_monday" value="0"></td>
                                      <td><input class="input-slot" type="number" name="complexity_monday" value="0"></td>
                                    </tr>
                                    <tr>
                                      <td><h3>
                                        ВТ
                                        </h3></td>
                                      <td><input class="input-slot" type="number" name="lessons_tuesday" value="0"></td>
                                      <td><input class="input-slot" type="number" name="complexity_tuesday" value="0"></td>
                                    </tr>
                                    <tr>
                                      <td><h3>
                                        СР
                                        </h3></td>
                                      <td><input class="input-slot" type="number" name="lessons_wednesday" value="0"></td>
                                      <td><input class="input-slot" type="number" name="complexity_wednesday" value="0"></td>
                                    </tr>
                                    <tr>
                                      <td><h3>
                                        ЧТ
                                        </h3></td>
                                      <td><input class="input-slot" type="number" name="lessons_thursday" value="0"></td>
                                      <td><input class="input-slot" type="number" name="complexity_thursday" value="0"></td>
                                    </tr>
                                    <tr>
                                      <td><h3>
                                        ПТ
                                        </h3></td>
                                      <td><input class="input-slot" type="number" name="lessons_friday" value="0"></td>
                                      <td><input class="input-slot" type="number" name="complexity_friday" value="0"></td>
                                    </tr>
                                    <tr>
                                      <td><h3>
                                        СБ
                                        </h3></td>
                                      <td><input class="input-slot" type="number" name="lessons_saturday" value="0"></td>
                                      <td><input class="input-slot" type="number" name="complexity_saturday" value="0"></td>
                                    </tr>
                                </table>
                            </div>
                                <div class="footer">
                                    <div class="footer-buttons-container">
                                        <button type="submit" class="footer-button" onclick="window.location.href='/schedule'">
                                            <h4>Сохранить</h4>
                                        </button>
                                        <button class="footer-button" type="button" onclick="window.history.back()"><h4>Назад</h4></button>
                                    </div>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </selection>
        </body>
</html>
