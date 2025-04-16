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
        <section>
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
                <div class="body">
                    <div class="header-create icon-back">
                        <h2>Редактирование данных</h2>
                        <button class="button-none" type="button" onclick="window.history.back()">
                            <img src="/images/back.svg" alt="Аккаунт" width="25" height="25">
                        </button>
                    </div>
                    <div class="main">
                        <form action="/teacher/create" method="post">
                            <div class="input-block">
                                <h5>ФИО преподавателя</h5><input type="text" name="name" placeholder="Введите  ФИО"/>
                            </div>
                            <div class="input-block">
                                <h5>Почта</h5><input type="text" name="email" placeholder="Введите  почту"/>
                            </div>
                            <div class="input-block">
                                <h5>Пароль</h5><input type="text" name="password" placeholder="Введите  пароль"/>
                            </div>

                             <div class="input-block">
                                  <h5>Предметы</h5>
                                  <div class="select-block">
                                      <select name="courseId" multiple>
                                        <option value="" disabled selected >Выберите предметы</option>
                                          <#list courses as course>
                                              <option value="${course.courseId}">${course.name}</option>
                                          </#list>
                                      </select><br>
                                  </div>
                              </div>


                            <div class="input-block">
                                <h5>Кол-во пар в день</h5><input type="text" name="maxNumberOfLessons" placeholder="Введите кол-во пар в день"/>
                            </div>
                            <div class="input-block">
                                <h5>Должность</h5><input type="text" name="degree" placeholder="Введите должность"/>
                            </div>
                           <div class="input-block">
                                <h5>Временной слот</h5><input type="text" name="timeslotId" placeholder="Тут будет временной слот"/>
                            </div>


                            <div class="input-block">
                              <h5>Доступные временные слоты</h5>
                              <div class="availability-grid">
                                <table>
                                  <thead>
                                    <tr>
                                      <th></th>
                                      <th>ПН</th>
                                      <th>ВТ</th>
                                      <th>СР</th>
                                      <th>ЧТ</th>
                                      <th>ПТ</th>
                                      <th>СБ</th>
                                    </tr>
                                  </thead>
                                  <tbody id="grid-body">
                                    <!-- сюда будет вставлена таблица -->
                                  </tbody>
                                </table>
                                <div class="color-legend">
                                  <div class="color-option level-2" data-level="2" title="Высокий приоритет"></div>
                                  <div class="color-option level-1" data-level="1" title="Средний приоритет"></div>
                                  <div class="color-option level-0" data-level="0" title="Низкий приоритет"></div>
                                  <div class="color-option level-disabled" data-level="3" title="Недоступно"></div>
                                </div>
                              </div>
                            </div>

                            <!-- скрытое поле для отправки -->
                            <input type="hidden" name="availabilityGrid" id="availabilityGrid">


                            <div class="footer">
                            <div class="footer-buttons-container">

                                <button class="footer-button" type="button" onclick="window.history.back()">
                                    <h4>Назад</h4>
                                </button>
                              <div class="footer-right">
                                    <button type="submit" class="footer-button save-button">
                                        <h4>Сохранить</h4>
                                    </button>
                              </div>
                            </div>
                        </div>
                        </form>
                    </div>
                </div>
            </div>
        </section>
        <script>
          document.addEventListener("DOMContentLoaded", () => {
            const days = ['ПН', 'ВТ', 'СР', 'ЧТ', 'ПТ', 'СБ'];
            const lessons = 7;
            const gridBody = document.getElementById('grid-body');
            const selectedColorInput = document.getElementById("selectedColorLevel");
            const hiddenInput = document.getElementById("availabilityGrid");
            const colorOptions = document.querySelectorAll(".color-option");
            let selectedLevel = "0";

            // Сгенерировать таблицу
            for (let i = 1; i <= lessons; i++) {
              const tr = document.createElement('tr');
              const numberCell = document.createElement('td');
              numberCell.textContent = i;
              tr.appendChild(numberCell);

              for (let j = 0; j < days.length; j++) {
                const td = document.createElement('td');
                td.classList.add('cell');
                td.dataset.level = "0";
                td.dataset.day = j;
                td.dataset.lesson = i;
                tr.appendChild(td);
              }
              gridBody.appendChild(tr);
            }

            // Переключение выбранного цвета
            colorOptions.forEach(option => {
              option.addEventListener("click", () => {
                colorOptions.forEach(opt => opt.classList.remove("selected"));
                option.classList.add("selected");
                selectedLevel = option.getAttribute("data-level");
              });
            });

            // Клик по ячейке: назначаем цвет и сохраняем данные
            gridBody.addEventListener("click", (e) => {
              if (e.target.classList.contains('cell')) {
                e.target.dataset.level = selectedLevel;
                updateHiddenInput();
              }
            });

            function updateHiddenInput() {
              const data = [];
              const cells = document.querySelectorAll('.cell');
              cells.forEach(cell => {
                data.push({
                  day: parseInt(cell.dataset.day),
                  lesson: parseInt(cell.dataset.lesson),
                  level: parseInt(cell.dataset.level)
                });
              });
              hiddenInput.value = JSON.stringify(data);
            }

            // начальное обновление
            updateHiddenInput();
          });
        </script>


    </body>
</html>
