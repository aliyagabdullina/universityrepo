<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link href="https://fonts.googleapis.com/css2?family=Roboto&display=swap" rel="stylesheet">
        <link rel="stylesheet" href="/styles/styleDataPages.css">
        <link rel="stylesheet" href="/styles/FloatingButton.css">

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
                        <button class="menu-button icon highlight" onclick="window.location.href='/data'">
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
                <div class="frame">
                    <div class="header">
                    <h2>Данные об индивидуальных планах</h2>
                    <div class="spacer"></div>
                    <div class="custom-select">
                        <select onchange="if(this.value === '1') window.location.href='/places';
                                        else if(this.value === '2') window.location.href='/courses';
                                        else if(this.value === '3') window.location.href='/teachers';
                                        else if(this.value === '4') window.location.href='/students';
                                        else if(this.value === '5') window.location.href='/groups';
                                        else if(this.value === '6') window.location.href='/programs';
                                        else if(this.value === '7') window.location.href='/plan'">
                            <option value="" disabled selected class="placeholder">Раздел</option>
                            <option value="1">Аудитории</option>
                            <option value="2">Предметы</option>
                            <option value="3">Преподаватели</option>
                            <option value="4">Студенты</option>
                            <option value="5">Группы</option>
                            <option value="6">Программы</option>
                            <option value="7">Индивидуальный план</option>
                        </select>
                    </div>
                </div>
                    <div class="main">
                        <div class="sorting">
                            <button id="sortButton" class="menu-button icon sorter-button">
                                <h4>Сортировка</h4>
                                <div class="spacer-button"></div>
                                <img src="/images/arrow.svg" alt="Аккаунт" width="25" height="25">
                            </button>
                            <select id="sortOptions" style="display: none;">
                              <option value="name">По имени</option>
                              <option value="date">По дате</option>
                              <option value="size">По размеру</option>
                            </select>
                        </div>
                        <div class="spacer-bottom"></div>
                        <div class="table-style">
                            <table>
                                <tr>
                                    <th>Cтудент</th>
                                    <th>Программа</th>
                                    <th>Доп. предметы</th>
                                    <th>Кол-во часов</th>
                                </tr>
                                <#list groups as group>
                                    <tr>
                                        <td><#list students as student>
                                                  ${student.name},
                                            </#list>
                                        </td>
                                        <td><#list programs as program>
                                                  ${program.name},
                                            </#list>
                                        </td>
                                        <td><#list courses as course>
                                                  ${course.name},
                                            </#list>
                                        </td>
                                        <td>${group.hours}</td>

                                    </tr>
                                </#list>
                            </table>
                        </div>
                    </div>
                    <div class="footer">
                        <div class="footer-buttons-container">
                            <button class="footer-button" onclick="window.location.href='/plan/create'">
                                <h4>Редактировать</h4>
                            </button>
                            <button class="footer-button" onclick="window.location.href='/plan/create'">
                                <h4>Добавить</h4>
                            </button>
                            <button class="footer-button" onclick="window.location.href='/plan/create'">
                                <h4>Удалить</h4>
                            </button>
                            <form id="importForm" action="/plan/import" method="post" enctype="multipart/form-data">
                                <input type="file" name="file" id="fileInput" accept=".csv">
                            </form>

                            <label for="fileInput" class="footer-button">
                                 <h4>Импортировать</h4>
                            </label>
                        </div>
                    </div>
                </div>
            </div>
        </selection>

        <!-- Подключаем чат-бот через внешний файл -->
        <#include "chatbot.html">
        <script src="/scripts/chatbot.js"></script>
    </body>
</html>