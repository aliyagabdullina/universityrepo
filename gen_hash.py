import bcrypt

password = b"test_password"
hashed = bcrypt.hashpw(password, bcrypt.gensalt())
print(hashed.decode())
