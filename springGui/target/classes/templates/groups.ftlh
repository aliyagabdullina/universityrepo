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
                    <div class="menu-buttons-container">
                        <button class="menu-button icon" onclick="window.location.href='/account'">
                            <img src="/images/account.svg" alt="Аккаунт" width="25" height="25">
                            <h4>Аккаунт</h4>
                        </button>
                        <button class="menu-button icon" onclick="window.location.href='/timeslot'">
                            <img src="/images/time.svg" alt="Аккаунт" width="25" height="25">
                            <h4>Тайм слоты</h4>
                        </button>
                        <button class="menu-button icon highlight" onclick="window.location.href='/places'">
                            <img src="/images/data.svg" alt="Аккаунт" width="25" height="25">
                            <h4>Редактирование данных</h4>
                        </button>
                        <button class="menu-button icon" onclick="window.location.href='/schedule'">
                            <img src="/images/scheduling.svg" alt="Аккаунт" width="25" height="25">
                            <h4>Расписание</h4>
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
                        <h2>Редактирование данных</h2>
                        <div class="spacer"></div>
                        <div class="buttons-container">
                            <button onclick="window.location.href='/places'">
                                <h4>Кабинеты</h4>
                            </button>
                            <button onclick="window.location.href='/courses'">
                                <h4>Курсы</h4>
                            </button>
                            <button onclick="window.location.href='/teachers'">
                                <h4>Учителя</h4>
                            </button>
                            <button onclick="window.location.href='/groups'">
                                <h4>Группы</h4>
                            </button>
                            <button onclick="window.location.href='/programs'">
                                <h4>Программы</h4>
                            </button>
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
                                    <th>Группа</th>
                                    <th>Кол-во студентов</th>
                                    <th>Кабинеты</th>
                                    <th>Макс уроков</th>
                                    <th>Учителя</th>
                                    <th>Временной слот</th>
                                </tr>
                                <#list groups as group>
                                    <tr>
                                        <td>${group.name}</td>
                                        <td>${group.numOfStudents}</td>
                                        <td>${group.placeId}</td>
                                        <td>${group.maxNumberOfLessons}</td>
                                        <td><#list teachers as teacher>
                                                  ${teacher.name},
                                            </#list>
                                        </td>
                                        <td>${group.timeslotId}</td>
                                    </tr>
                                </#list>
                            </table>
                        </div>
                    </div>
                    <div class="footer">
                        <div class="footer-buttons-container">
                            <button class="footer-button" onclick="window.location.href='/group/create'">
                                <h4>Редактировать</h4>
                            </button>
                            <button class="footer-button" onclick="window.location.href='/group/create'">
                                <h4>Добавить</h4>
                            </button>
                            <button class="footer-button" onclick="window.location.href='/group/create'">
                                <h4>Удалить</h4>
                            </button>
                            <form id="importForm" action="/group/import" method="post" enctype="multipart/form-data">
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
    </body>
</html>
