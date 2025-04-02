<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Account Page</title>
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
                    <button class="menu-button icon highlight" onclick="window.location.href='/account'">
                        <img src="/images/account.svg" alt="Аккаунт" width="25" height="25">
                        <h4>Аккаунт</h4>
                    </button>
                    <button class="menu-button icon" onclick="window.location.href='/schedule'">
                        <img src="/images/scheduling.svg" alt="Расписание" width="25" height="25">
                        <h4>Расписание</h4>
                    </button>
                    <button class="menu-button icon" onclick="window.location.href='/places'">
                        <img src="/images/data.svg" alt="Данные" width="25" height="25">
                        <h4>Данные</h4>
                    </button>
                    <button class="menu-button icon" onclick="window.location.href='/timeslot'">
                        <img src="/images/time.svg" alt="Сетка пар" width="25" height="25">
                        <h4>Сетка пар</h4>
                    </button>
                </div>
                <div class="menu-buttons-container-bottom">
                    <button class="menu-button icon" onclick="window.location.href='/settings'">
                        <img src="/images/setting.svg" alt="Настройки" width="25" height="25">
                        <h4>Настройки</h4>
                    </button>
                </div>
            </div>
            <div></div>
            <div class="frame">
                <div class="header">
                    <h2>Аккаунт</h2>
                </div>
                <div class="main">
                    <div class="profile">
                        <img src="https://via.placeholder.com/50" class="profile-pic" id="profilePic" onclick="document.getElementById('fileInput').click()">
                        <input type="file" id="fileInput" accept="image/*" style="display: none;" onchange="previewImage(event)">
                        <div class="details">
                            <!-- Данные подтягиваются из базы -->
                            <p class="job-title" id="positionUniversity">РАБОТНИК НИУ ВШЭ МОСКВА</p>
                            <h3 class="black" id="name">Остапец Валерия</h3>
                            <p class="group" id="faculty">ФКН</p>
                        </div>
                    </div>
                </div>
                <div class="footer">
                    <div class="footer-buttons-container">
                        <button class="footer-button" onclick="window.location.href='/logout'">
                            <h4>Выйти из аккаунта</h4>
                        </button>
                    </div>
                </div>
            </div>
        </div>
    </section>

    <script>
        // Предпросмотр загруженного изображения
        function previewImage(event) {
            const file = event.target.files[0];
            if (file) {
                const reader = new FileReader();
                reader.onload = function(e) {
                    document.getElementById('profilePic').src = e.target.result;
                    // Здесь можно добавить код для отправки изображения на сервер
                    // saveImageToServer(file);
                };
                reader.readAsDataURL(file);
            }
        }

        // Пример подтягивания данных из базы (имитация)
        function loadUserData() {
            // Это пример данных, в реальном приложении они придут с сервера
            const userData = {
                positionUniversity: "РАБОТНИК НИУ ВШЭ МОСКВА", // Должность + Университет
                name: "Остапец Валерия",
                faculty: "ФКН",
                photo: "https://via.placeholder.com/50" // начальное фото
            };

            document.getElementById("positionUniversity").textContent = userData.positionUniversity;
            document.getElementById("name").textContent = userData.name;
            document.getElementById("faculty").textContent = userData.faculty;
            document.getElementById("profilePic").src = userData.photo;
        }

        // Загрузка данных при открытии страницы
        window.onload = loadUserData;

        // Пример функции для отправки изображения на сервер (нужен backend)
        /*
        function saveImageToServer(file) {
            const formData = new FormData();
            formData.append("profilePic", file);
            fetch('/upload-profile-pic', {
                method: 'POST',
                body: formData
            })
            .then(response => response.json())
            .then(data => console.log("Фото сохранено:", data))
            .catch(error => console.error("Ошибка:", error));
        }
        */
    </script>
</body>
</html>