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
                        <button class="menu-button icon highlight" onclick="window.location.href='/timeslot'">
                            <img src="/images/time.svg" alt="Аккаунт" width="25" height="25">
                            <h4>Тайм слоты</h4>
                        </button>
                        <button class="menu-button icon" onclick="window.location.href='/places'">
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
                        <h2>Тайм слоты</h2>
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
                                    <th>Название</th>
                                    <th>Время создания</th>
                                    <th>Дата создания</th>
                                </tr>

                                <tr>
                                    <td>Документ 1</td>
                                    <td>10:00</td>
                                    <td>2024-03-14</td>
                                  </tr>
                                  <tr>
                                    <td>Документ 2</td>
                                    <td>12:30</td>
                                    <td>2024-03-15</td>
                                  </tr>
                                  <tr>
                                    <td>Документ 3</td>
                                    <td>14:45</td>
                                    <td>2024-03-16</td>
                                  </tr>
                            </table>
                        </div>
                    </div>
                    <div class="footer">
                        <div class="footer-buttons-container">
                            <button class="footer-button" onclick="window.location.href='/timeslot-create'">
                                <h4>Редактировать</h4>
                            </button>
                            <button class="footer-button" onclick="window.location.href='/timeslot-create'">
                                <h4>Добавить</h4>
                            </button>
                            <button class="footer-button" onclick="window.location.href='/timeslot-create'">
                                <h4>Удалить</h4>
                            </button>
                            <button class="footer-button" onclick="window.location.href='/timeslot-create'">
                                <h4>Импортировать</h4>
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </selection>
    </body>
</html>
