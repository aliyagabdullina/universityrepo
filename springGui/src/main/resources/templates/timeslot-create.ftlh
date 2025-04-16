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
                        <button class="menu-button icon" onclick="window.location.href='/data'">
                            <img src="/images/data.svg" alt="Аккаунт" width="25" height="25">
                            <h4>Данные</h4>
                        </button>
                        <button class="menu-button icon highlight" onclick="window.location.href='/timeslot'">
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
                    <div class="header-create icon-back">
                        <h2>Редактирование данных</h2>
                        <button class="button-none" type="button" onclick="window.history.back()">
                            <img src="/images/back.svg" alt="Аккаунт" width="25" height="25">
                        </button>
                    </div>
                    <div class="main">
                        <div class="line">
                          <div class="input-block">
                              <h5>Название слота</h5><input type="text" name="name" placeholder="Введите название"/>
                          </div>
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
                      </div>
                        <div class="spacer-bottom"></div>
                      <form action="/places" method="post">
                        <div class="table-style table-program">
                            <table id="timeslotTable">
                              <tr>
                                  <th>Номер урока</th>
                                  <th>Начало урока</th>
                                  <th>Конец урока</th>
                              </tr>
                              <tr>
                                  <td><input class="input-slot" type="text" value="1"></td>
                                  <td><input class="input-slot" type="text" value="08:00"></td>
                                  <td><input class="input-slot" type="text" value="08:45"></td>
                              </tr>
                              <tr>
                                  <td><input class="input-slot" type="text" value="2"></td>
                                  <td><input class="input-slot" type="text" value="09:00"></td>
                                  <td><input class="input-slot" type="text" value="09:45"></td>
                              </tr>
                              <tr>
                                  <td><input class="input-slot" type="text" value="3"></td>
                                  <td><input class="input-slot" type="text" value="10:00"></td>
                                  <td><input class="input-slot" type="text" value="10:45"></td>
                              </tr>
                              <tr>
                                  <td><input class="input-slot" type="text" value="4"></td>
                                  <td><input class="input-slot" type="text" value="11:00"></td>
                                  <td><input class="input-slot" type="text" value="11:45"></td>
                              </tr>
                              <tr>
                                  <td><input class="input-slot" type="text" value="5"></td>
                                  <td><input class="input-slot" type="text" value="12:00"></td>
                                  <td><input class="input-slot" type="text" value="12:45"></td>
                              </tr>
                              <tr>
                                  <td><input class="input-slot" type="text" value="6"></td>
                                  <td><input class="input-slot" type="text" value="13:00"></td>
                                  <td><input class="input-slot" type="text" value="13:45"></td>
                              </tr>
                              <tr>
                                  <td><input class="input-slot" type="text" value="7"></td>
                                  <td><input class="input-slot" type="text" value="14:00"></td>
                                  <td><input class="input-slot" type="text" value="14:45"></td>
                              </tr>
                              <tr>
                                  <td><input class="input-slot" type="text" value="8"></td>
                                  <td><input class="input-slot" type="text" value="15:00"></td>
                                  <td><input class="input-slot" type="text" value="15:45"></td>
                              </tr>
                          </table>
                        </div>
                        <div class="footer">
                        <div class="footer-buttons-container">
                            <button type="submit" class="footer-button" onclick="window.location.href='/places'">
                                <h4>Сохранить</h4>
                            </button>
                            <button class="footer-button" type="button" onclick="window.history.back()"><h4>Назад</h4></button>
                        </div>
                    </div>
                      </form>
                    </div>
                </div>
            </div>
          <script>
            // Add event listener to table cells
            const table = document.getElementById('timeslotTable');
            table.addEventListener('input', function(event) {
              const target = event.target;
              if (target.tagName === 'INPUT') {
                const row = target.parentNode.parentNode;
                const id = row.cells[0].querySelector('input').value;
                const startTime = row.cells[1].querySelector('input').value;
                const endTime = row.cells[2].querySelector('input').value;

                console.log('ID:', id, 'Start Time:', startTime, 'End Time:', endTime);
              }
            });
          </script>
        </selection>
    </body>
</html>
