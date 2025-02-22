<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link href="https://fonts.googleapis.com/css2?family=Roboto&display=swap" rel="stylesheet">
        <link rel="stylesheet" href="/styles/styleDataPages.css">
        <link rel="stylesheet" href="/styles/styleRectangle.css">
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
                                                    <h2>Расписание</h2>
                                                    <div class="sorting-buttons">
                                                      <div class="select-block select-block-schedule">
                                                      <form id="groupForm">
                                                        <select id="groupSelect" onchange="redirectToGroupPage()">
                                                          <option value="">Группа</option>
                                                          <#list groups as group>
                                                                 <option value="/schedule/group/10-2">${group.name}</option>,
                                                          </#list>
                                                        </select>
                                                      </form>
                                                  </div>
                                                      <script>
                                                        function redirectToGroupPage() {
                                                          var selectElement = document.getElementById("groupSelect");
                                                          var selectedGroup = selectElement.value;
                                                          if (selectedGroup) {
                                                            window.location.href = selectedGroup;
                                                          }
                                                        }
                                                      </script>
                                                                                  <div class="spacer">

                                                      </div>
                                                    <div class="select-block select-block-schedule">
                                                      <form id="teacherForm">
                                                        <select id="teacherSelect" onchange="redirectToTeacherPage()">
                                                          <option value="">Учитель</option>
                                                          <#list teachers as teacher>
                                                               <option value="/schedule/teacher">${teacher.name}</option>,
                                                           </#list>

                                                        </select>
                                                      </form>
                                                  </div>
                                                      <script>
                                                        function redirectToTeacherPage() {
                                                          var selectElement = document.getElementById("teacherSelect");
                                                          var selectedTeacher = selectElement.value;
                                                          if (selectedTeacher) {
                                                            window.location.href = selectedTeacher;
                                                          }
                                                        }
                                                      </script>
                                                      <div class="spacer">

                                                      </div>
                                                      <div class="select-block select-block-schedule">
                                                      <form id="placeForm">
                                                        <select id="placeSelect" onchange="redirectToPlacePage()">
                                                          <option value="">Кабинет</option>
                                                          <#list places as place>
                                                             <option value="/schedule/place">${place.name}</option>,
                                                          </#list>
                                                        </select>
                                                      </form>
                                                  </div>
                                                      <script>
                                                        function redirectToPlacePage() {
                                                          var selectElement = document.getElementById("placeSelect");
                                                          var selectedPlace = selectElement.value;
                                                          if (selectedPlace) {
                                                            window.location.href = selectedPlace;
                                                          }
                                                        }
                                                      </script>
                                                  </div>
                                                </div>
                        <div class="main">
                            <div class="centre-text">
                                10-2
                            </div>
                            <div class="spacer-bottom"></div>
                            <div class="table-style">
                                <table>
                                    <tr>
                                        <th></th>
                                        <th><h3>
                                           ПН
                                          </h3></th>
                                        <th><h3>
                                           ВТ
                                          </h3></th>
                                        <th><h3>
                                           СР
                                          </h3></th>
                                        <th><h3>
                                           ЧТ
                                          </h3></th>
                                        <th><h3>
                                           ПТ
                                          </h3></th>
                                        <th><h3>
                                           СБ
                                          </h3></th>
                                    </tr>
                                    <#list array as row>
                                            <#if row?has_content> <!-- Проверка на наличие данных в строке -->
                                                    <tr>
                                                        <td>${row[0]!''}</td> <!-- Проверка на null и использование пустой строки в случае отсутствия -->
                                                        <#list row[1..] as cell>
                                                            <td>${cell!''}</td>
                                                        </#list>
                                                    </tr>
                                                    <#else>
                                                            <tr>
                                                                <td colspan="7">-</td> <!-- Если нет данных в строке, то выводится "-" в одной ячейке -->
                                                            </tr>
                                                </#if>
                                        </#list>
                                  </table>
                            </div>
                        </div>
                        <div class="footer">
                                <button class="footer-button" onclick="window.location.href='/schedule'">
                                    <h4>Импортировать</h4>
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            </selection>
        </body>
</html>
