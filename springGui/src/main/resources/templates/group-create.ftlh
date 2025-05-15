<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8" />
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <link href="https://fonts.googleapis.com/css2?family=Roboto&display=swap" rel="stylesheet" />
    <link rel="stylesheet" href="/styles/styleDataPages.css" />
    <link rel="stylesheet" href="/styles/styleCreate.css" />
    <link rel="stylesheet" href="/styles/styleDataWithTimeSlots.css">
    <style>
        /* Таблица для тайм-слотов */
        .timeslot-table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 10px;
        }
        .timeslot-table th, .timeslot-table td {
            border: 1px solid #ccc;
            padding: 8px;
            text-align: center;
            font-family: 'Roboto', sans-serif;
            font-size: 14px;
        }
        .timeslot-table th {
            background-color: #f0f0f0;
        }
        .timeslot-radio {
            cursor: pointer;
            transform: scale(1.2);
        }
    </style>
</head>
<body>
    <section>
        <div class="container">
            <div class="menu">
                <h1>Scheduling</h1>
                <p class="university">UNIVERSITY</p>
                <div class="menu-buttons-container">
                    <button class="menu-button icon" onclick="window.location.href='/account'">
                        <img src="/images/account.svg" alt="Аккаунт" width="25" height="25" />
                        <h4>Аккаунт</h4>
                    </button>
                    <button class="menu-button icon" onclick="window.location.href='/schedule'">
                        <img src="/images/scheduling.svg" alt="Аккаунт" width="25" height="25" />
                        <h4>Расписание</h4>
                    </button>
                    <button class="menu-button icon highlight" onclick="window.location.href='/data'">
                        <img src="/images/data.svg" alt="Аккаунт" width="25" height="25" />
                        <h4>Данные</h4>
                    </button>
                    <button class="menu-button icon" onclick="window.location.href='/timeslot'">
                        <img src="/images/time.svg" alt="Аккаунт" width="25" height="25" />
                        <h4>Сетка пар</h4>
                    </button>
                </div>
                <div class="menu-buttons-container-bottom">
                    <button class="menu-button icon" onclick="window.location.href='/settings'">
                        <img src="/images/setting.svg" alt="Аккаунт" width="25" height="25" />
                        <h4>Настройки</h4>
                    </button>
                </div>
            </div>
            <div></div>
            <div class="body">
                <div class="header-create icon-back">
                    <h2>Редактирование данных о группах</h2>
                    <button class="button-none" type="button" onclick="window.history.back()">
                        <img src="/images/back.svg" alt="Аккаунт" width="25" height="25" />
                    </button>
                </div>
                <div class="main">
                    <form action="/teacher/create" method="post">
                        <div class="input-block">
                            <h5>Название группы</h5>
                                <input type="text" name="name" placeholder="Введите группу" required
                                       value="<#if group?? && group.name??>${group.name}</#if>">
                        </div>
                        <div class="spacer-bottom"></div>
                        <div class="input-block">
                            <h5>Студенты</h5>
                                <input type="text" name="numOfStudents" placeholder="Введите кол-во студентов" required
                                       value="<#if group?? && group.numOfStudents??>${group.numOfStudents}</#if>">
                        </div>


                        //селектор
                        <div class="spacer-bottom"></div>
                        <div class="input-block">
                            <h5>Преподаватели</h5>
                            <div class="custom-multiselect" id="teacher-multiselect">
                                <div class="selected-options">Выберите преподавателей</div>
                                <div class="options-list">
                                    <#list teachers as teacher>
                                        <div class="option" data-value="${teacher.teacherId}">${teacher.name}</div>
                                    </#list>
                                </div>
                                <input type="hidden" name="teacherId" id="teacher-hidden-input" />
                            </div>
                        </div>



                        <div class="spacer-bottom"></div>
                        <div class="input-block">
                            <h5>Максимум пар в день</h5>
                                <input type="text" name="maxNumberOfLessons" placeholder="Введите кол-во макс пар" required
                                       value="<#if group?? && group.maxNumberOfLessons??>${group.maxNumberOfLessons}</#if>">
                        </div>

                        <div class="spacer-bottom"></div>
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
                              <div class="color-option-wrapper" data-tooltip="Не подходит">
                                <div class="color-option level-2" data-level="3"></div>
                              </div>
                              <div class="color-option-wrapper" data-tooltip="Не желательно">
                                <div class="color-option level-1" data-level="2"></div>
                              </div>
                              <div class="color-option-wrapper" data-tooltip="Желательно">
                                <div class="color-option level-0" data-level="1"></div>
                              </div>
                              <div class="color-option-wrapper" data-tooltip="Ничего не выбрано">
                                <div class="color-option level-disabled" data-level="0"></div>
                              </div>
                            </div>
                          </div>
                        </div>
                        <input type="hidden" name="availabilityGrid" id="availabilityGrid">
                    </form>
                </div>
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
            </div>
        </div>
    </section>
    <script>
        document.addEventListener("DOMContentLoaded", function () {
            const multiselect = document.getElementById("teacher-multiselect");
            const selected = multiselect.querySelector(".selected-options");
            const optionsList = multiselect.querySelector(".options-list");
            const hiddenInput = document.getElementById("teacher-hidden-input");

            const selectedValues = new Set();

            selected.addEventListener("click", () => {
                optionsList.classList.toggle("show");
            });

            optionsList.addEventListener("click", (e) => {
                if (e.target.classList.contains("option")) {
                    const value = e.target.dataset.value;

                    if (selectedValues.has(value)) {
                        selectedValues.delete(value);
                        e.target.classList.remove("selected");
                    } else {
                        selectedValues.add(value);
                        e.target.classList.add("selected");
                    }

                    updateSelectedDisplay();
                }
            });

            function updateSelectedDisplay() {
                const selectedArray = Array.from(selectedValues);
                if (selectedArray.length === 0) {
                    selected.textContent = "Выберите преподавателей";
                } else {
                    selected.textContent = selectedArray.length + " выбрано";
                }
                hiddenInput.value = selectedArray.join(",");
            }

            document.addEventListener("click", function (e) {
                if (!multiselect.contains(e.target)) {
                    optionsList.classList.remove("show");
                }
            });
        });
        document.addEventListener("DOMContentLoaded", () => {
          const days = ['ПН', 'ВТ', 'СР', 'ЧТ', 'ПТ', 'СБ'];
          const lessons = 7;
          const gridBody = document.getElementById('grid-body');
          const hiddenInput = document.getElementById("availabilityGrid");
          const colorOptions = document.querySelectorAll(".color-option");
          let selectedLevel = "0";

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

          colorOptions.forEach(option => {
            option.addEventListener("click", () => {
              colorOptions.forEach(opt => opt.classList.remove("selected"));
              option.classList.add("selected");
              selectedLevel = option.getAttribute("data-level");
            });
          });

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

          updateHiddenInput();
        });

    </script>
</body>
</html>
