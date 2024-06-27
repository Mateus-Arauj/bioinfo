import socket

def start_client(host='127.0.0.1', port=65432):
    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as client_socket:
        client_socket.connect((host, port))
        
        sequence = client_socket.recv(1024).decode()
        print("Sequence received from server:", sequence)
        

        response_message = "Exemplo de resposta"
        client_socket.sendall(response_message.encode())
        print("Response sent to server")

if __name__ == '__main__':
    start_client()
