import socket
import threading
import random
import time

def handle_client(client_socket):
    request = client_socket.recv(1024).decode()
    print(f"Received request: {request}")
    sequences = request.split(";")
    sequence1 = sequences[0]
    sequence2 = sequences[1]

    # Simulação de múltiplos clients (substitua pelo cálculo real)
    mock_results = []
    for i in range(3):  # Simulando três clientes
        score_n = random.randint(50, 100)
        num_gaps_n = random.randint(0, 10)
        execution_time_n = round(random.uniform(0.1, 0.5), 6)
        evalue_n = round(random.uniform(0.001, 0.01), 6)
        score_s = random.randint(50, 100)
        num_gaps_s = random.randint(0, 10)
        execution_time_s = round(random.uniform(0.1, 0.5), 6)
        evalue_s = round(random.uniform(0.001, 0.01), 6)
        mock_result = f"Python;LinesOfCode:200;Needleman;AlignmentScore:{score_n};Gap:{num_gaps_n};ExecutionTime:{execution_time_n};EValue:{evalue_n};Smith;AlignmentScore:{score_s};Gap:{num_gaps_s};ExecutionTime:{execution_time_s};EValue:{evalue_s}"
        mock_results.append(mock_result)
        time.sleep(0.1)  # Simulando o tempo de resposta de cada cliente

    # Selecionar o melhor resultado baseado no AlignmentScore do Needleman-Wunsch
    best_result = max(mock_results, key=lambda x: int(x.split(";")[3].split(":")[1]))
    print(f"Sending best result: {best_result}")
    client_socket.send(best_result.encode())
    client_socket.close()

def start_server():
    server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    server.bind(("0.0.0.0", 65431))
    server.listen(5)
    print("Python Server listening on port 65431")

    while True:
        client_socket, addr = server.accept()
        client_handler = threading.Thread(target=handle_client, args=(client_socket,))
        client_handler.start()

if __name__ == "__main__":
    start_server()
