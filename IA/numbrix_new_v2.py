# Grupo 23:
# 96832 Afonso Ferreira
# 96898 Miguel Gomes

import sys
from search import Problem, Node, astar_search, breadth_first_tree_search, depth_first_tree_search, greedy_search, \
    recursive_best_first_search, compare_searchers_numbrix


import time
import psutil


class NumbrixState:
    state_id = 0

    __slots__ = ["id", "board"]
    def __init__(self, board):
        self.board = board
        self.id = NumbrixState.state_id
        #self.last = []
        NumbrixState.state_id += 1

    def __lt__(self, other):
        return self.id < other.id

    def __str__(self):
        return ""
        # return self.board.to_string()


class Board:
    """ Representação interna de um tabuleiro de Numbrix. """
    
    def get_number(self, row: int, col: int) -> int:
        """ Devolve o valor na respetiva posição do tabuleiro. """
        return self.board[row][col]
    
    def adjacent_vertical_numbers(self, row: int, col: int) -> (int, int):
        """ Devolve os valores imediatamente abaixo e acima, respectivamente. """
        return (self.board[row + 1][col] if 0 <= row + 1 < self.size and 0 <= col < self.size else None,
                self.board[row - 1][col] if 0 <= row - 1 < self.size and 0 <= col < self.size else None)
    
    def adjacent_horizontal_numbers(self, row: int, col: int) -> (int, int):
        """ Devolve os valores imediatamente à esquerda e à direita, respectivamente. """
        return (self.board[row][col - 1] if 0 <= row < self.size and 0 <= col - 1 < self.size else None,
                self.board[row][col + 1] if 0 <= row < self.size and 0 <= col + 1 < self.size else None)
    
    @staticmethod    
    def parse_instance(filename: str):
        """ Lê o ficheiro cujo caminho é passado como argumento e retorna uma instância da classe Board. """
        positions = {}
        visited = []
        elems = []
        with open(filename) as f:
            for line in f:
                lin = line.rstrip("\n")
                tokens = lin.split("\t")
                elems += tokens
        size = int(elems[0])
        board = []
        for i in range(0, size):
            line = []
            for u in range(0, size):
                e = int(elems[1 + i * size + u])
                line += [e]
                if e != 0:
                    visited += [e]
                    positions[e] = (i, u)
            board += [line]
        visited.sort()
        return Board(size, board, positions, visited)

    __slots__ = ["size", "board", "positions", "to_visit"]
    def __init__(self, size, board, positions, to_visit):
        self.size = size
        self.board = board
        self.positions = positions
        self.to_visit = to_visit
        self.prune_to_visit()

    def to_string(self):
        out = ""
        for line in self.board:
            tmp_out = ""
            for elem in line:
                tmp_out += str(elem) + "\t"
            out += tmp_out[:-1] + "\n"
        return out[:-1]

    def __str__(self):
        return self.to_string()

    def change_value(self, row, col, val):
        self.board[row][col] = val
        self.positions[val] = (row, col)
        self.to_visit.append(val)
        self.prune_to_visit()

    def prune_to_visit(self):
        new_to_visit = []
        for num in self.to_visit:
            if num == 1 and 2 not in self.positions.keys():
                new_to_visit.append(1)
            elif num == self.size ** 2 and self.size ** 2 - 1 not in self.positions.keys():
                new_to_visit.append(self.size ** 2)
            elif 1 < num < self.size ** 2 and (num - 1 not in self.positions.keys() or num + 1 not in self.positions.keys()):
                new_to_visit.append(num)
        new_to_visit.sort()
        self.to_visit = new_to_visit


class Numbrix(Problem):
    def __init__(self, board: Board):
        """ O construtor especifica o estado inicial. """
        self.initial = NumbrixState(board)

    def actions(self, state: NumbrixState):
        """ Retorna uma lista de ações que podem ser executadas a partir do estado passado como argumento. """
        #if state.last:  # if not empty
         #   return state.last

        board = state.board
        size = board.size
        to_visit = board.to_visit
        #f.write("to_visit in begin actions in state " + str(state.id) + ": " + str(to_visit) + "\n")

        nums_to_explore = []
        #atomic = []
        min_actions_lens = size ** 2
        for num in to_visit:
            actions = self.analyze_pos(board, num)
            #f.write("actions for num " + str(num) + " " + str(actions) + "\n")

            if len(actions) == 0:
                return []
            elif len(actions) == 1:
                # atomic.append(actions)
                return actions
            else:
                if len(actions) < min_actions_lens:
                    min_actions_lens = len(actions)
                    nums_to_explore.clear()
                    nums_to_explore.append(num)
                elif len(actions) == min_actions_lens:
                    nums_to_explore.append(num)

        '''if atomic:  # if not empty
            if len(atomic) > 1:
                state.last = atomic[1]
            return atomic[0]'''

        if not nums_to_explore:  # if empty
            return []

        actions = []
        actions += self.analyze_pos(board, nums_to_explore[0])

        return actions

    def analyze_pos(self, board, num):
        row, col = board.positions[num]
        adjs = board.adjacent_vertical_numbers(row, col) + board.adjacent_horizontal_numbers(row, col)

        # confirm if this helps
        if 1 < num < board.size * board.size:
            if num - 1 not in adjs:
                if num + 1 not in adjs:
                    if adjs.count(0) < 2:
                        return []
                if adjs.count(0) < 1:
                    return []
            if num + 1 not in adjs:
                if adjs.count(0) < 1:
                    return []

        before, after = [], []
        if adjs[0] == 0:  # abaixo
            adjs_of_adj = board.adjacent_vertical_numbers(row + 1, col) + board.adjacent_horizontal_numbers(row + 1, col)

            if 0 not in adjs_of_adj:
                if num == 2 and 1 not in board.positions.keys():
                    before.append((row + 1, col, 1))
                if num == board.size ** 2 - 1 and board.size ** 2 not in board.positions.keys():
                    after.append((row + 1, col, board.size ** 2))

            if num - 1 > 0 and adjs_of_adj[0] == num - 2 and num - 1 not in board.positions.keys():  # abaixo
                before.append((row + 1, col, num - 1))
            elif num + 1 <= board.size ** 2 and adjs_of_adj[0] == num + 2 and num + 1 not in board.positions.keys():
                after.append((row + 1, col, num + 1))

            if num - 1 > 0 and adjs_of_adj[2] == num - 2 and num - 1 not in board.positions.keys():  # esquerda
                before.append((row + 1, col, num - 1))
            elif num + 1 <= board.size ** 2 and adjs_of_adj[2] == num + 2 and num + 1 not in board.positions.keys():
                after.append((row + 1, col, num + 1))

            if num - 1 > 0 and adjs_of_adj[3] == num - 2 and num - 1 not in board.positions.keys():  # direita
                before.append((row + 1, col, num - 1))
            elif num + 1 <= board.size ** 2 and adjs_of_adj[3] == num + 2 and num + 1 not in board.positions.keys():
                after.append((row + 1, col, num + 1))

            if num - 1 > 0 and num - 1 not in board.positions.keys():
                before.append((row + 1, col, num - 1))
            if num + 1 <= board.size ** 2 and num + 1 not in board.positions.keys():
                after.append((row + 1, col, num + 1))

        if adjs[1] == 0:  # acima
            adjs_of_adj = board.adjacent_vertical_numbers(row - 1, col) + board.adjacent_horizontal_numbers(row - 1, col)

            if 0 not in adjs_of_adj:
                if num == 2 and 1 not in board.positions.keys():
                    before.append((row - 1, col, 1))
                if num == board.size ** 2 - 1 and board.size ** 2 not in board.positions.keys():
                    after.append((row - 1, col, board.size ** 2))

            if num - 1 > 0 and adjs_of_adj[1] == num - 2 and num - 1 not in board.positions.keys():  # acima
                before.append((row - 1, col, num - 1))
            elif num + 1 <= board.size ** 2 and adjs_of_adj[1] == num + 2 and num + 1 not in board.positions.keys():
                after.append((row - 1, col, num + 1))

            if num - 1 > 0 and adjs_of_adj[2] == num - 2 and num - 1 not in board.positions.keys():  # esquerda
                before.append((row - 1, col, num - 1))
            elif num + 1 <= board.size ** 2 and adjs_of_adj[2] == num + 2 and num + 1 not in board.positions.keys():
                after.append((row - 1, col, num + 1))

            if num - 1 > 0 and adjs_of_adj[3] == num - 2 and num - 1 not in board.positions.keys():  # direita
                before.append((row - 1, col, num - 1))
            elif num + 1 <= board.size ** 2 and adjs_of_adj[3] == num + 2 and num + 1 not in board.positions.keys():
                after.append((row - 1, col, num + 1))

            if num - 1 > 0 and num - 1 not in board.positions.keys():
                before.append((row - 1, col, num - 1))
            if num + 1 <= board.size ** 2 and num + 1 not in board.positions.keys():
                after.append((row - 1, col, num + 1))

        if adjs[2] == 0:  # esquerda
            adjs_of_adj = board.adjacent_vertical_numbers(row, col - 1) + board.adjacent_horizontal_numbers(row, col - 1)

            if 0 not in adjs_of_adj:
                if num == 2 and 1 not in board.positions.keys():
                    before.append((row, col - 1, 1))
                if num == board.size ** 2 - 1 and board.size ** 2 not in board.positions.keys():
                    after.append((row, col - 1, board.size ** 2))

            if num - 1 > 0 and adjs_of_adj[0] == num - 2 and num - 1 not in board.positions.keys():  # abaixo
                before.append((row, col - 1, num - 1))
            elif num + 1 <= board.size ** 2 and adjs_of_adj[0] == num + 2 and num + 1 not in board.positions.keys():
                after.append((row, col - 1, num + 1))

            if num - 1 > 0 and adjs_of_adj[1] == num - 2 and num - 1 not in board.positions.keys():  # acima
                before.append((row, col - 1, num - 1))
            elif num + 1 <= board.size ** 2 and adjs_of_adj[1] == num + 2 and num + 1 not in board.positions.keys():
                after.append((row, col - 1, num + 1))

            if num - 1 > 0 and adjs_of_adj[2] == num - 2 and num - 1 not in board.positions.keys():  # esquerda
                before.append((row, col - 1, num - 1))
            elif num + 1 <= board.size ** 2 and adjs_of_adj[2] == num + 2 and num + 1 not in board.positions.keys():
                after.append((row, col - 1, num + 1))

            if num - 1 > 0 and num - 1 not in board.positions.keys():
                before.append((row, col - 1, num - 1))
            if num + 1 <= board.size ** 2 and num + 1 not in board.positions.keys():
                after.append((row, col - 1, num + 1))

        if adjs[3] == 0:  # direita
            adjs_of_adj = board.adjacent_vertical_numbers(row, col + 1) + board.adjacent_horizontal_numbers(row, col + 1)

            if 0 not in adjs_of_adj:
                if num == 2 and 1 not in board.positions.keys():
                    before.append((row, col + 1, 1))
                if num == board.size ** 2 - 1 and board.size ** 2 not in board.positions.keys():
                    after.append((row, col + 1, board.size ** 2))

            if num - 1 > 0 and adjs_of_adj[0] == num - 2 and num - 1 not in board.positions.keys():  # abaixo
                before.append((row, col + 1, num - 1))
            elif num + 1 <= board.size ** 2 and adjs_of_adj[0] == num + 2 and num + 1 not in board.positions.keys():
                after.append((row, col + 1, num + 1))

            if num - 1 > 0 and adjs_of_adj[1] == num - 2 and num - 1 not in board.positions.keys():  # acima
                before.append((row, col + 1, num - 1))
            elif num + 1 <= board.size ** 2 and adjs_of_adj[1] == num + 2 and num + 1 not in board.positions.keys():
                after.append((row, col + 1, num + 1))

            if num - 1 > 0 and adjs_of_adj[3] == num - 2 and num - 1 not in board.positions.keys():  # direita
                before.append((row, col + 1, num - 1))
            elif num + 1 <= board.size ** 2 and adjs_of_adj[3] == num + 2 and num + 1 not in board.positions.keys():
                after.append((row, col + 1, num + 1))

            if num - 1 > 0 and num - 1 not in board.positions.keys():
                before.append((row, col + 1, num - 1))
            if num + 1 <= board.size ** 2 and num + 1 not in board.positions.keys():
                after.append((row, col + 1, num + 1))

        before = list(dict.fromkeys([action for action in before if
                                     self.is_valid(board, action[0], action[1], action[2]) and
                                     self.test(board, action[0], action[1], action[2])]))
        after = list(dict.fromkeys([action for action in after if
                                    self.is_valid(board, action[0], action[1], action[2]) and
                                    self.test(board, action[0], action[1], action[2])]))

        if len(before) == 1:
            return before
        elif len(after) == 1:
            return after
        else:
            return before + after

    def test(self, board, row, col, val):
        for i in range(0, board.size):
            num = board.get_number(row, i)
            if num != 0:
                dist = abs(col - i)
                diff = abs(num - val)
                if dist == diff:
                    max_col = col if col > i else i
                    min_col = col if col <= i else i
                    for j in range(min_col+1, max_col):
                        if board.get_number(row, j) == 0 or val < board.get_number(row, j) < val + diff or\
                                val - diff < board.get_number(row, j) < val:
                            continue
                        else:
                            return False
        for i in range(0, board.size):
            num = board.get_number(i, col)
            if num != 0:
                dist = abs(row - i)
                diff = abs(num - val)
                if dist == diff:
                    max_row = row if row > i else i
                    min_row = row if row <= i else i
                    for j in range(min_row+1, max_row):
                        if board.get_number(j, col) == 0 or val < board.get_number(j, col) < val + diff or\
                                val - diff < board.get_number(j, col) < val:
                            continue
                        else:
                            return False
        return True

    def is_valid(self, board, r, c, n):
        for num in board.positions.keys():
            row, col = board.positions[num]
            if abs(row - r) + abs(col - c) > abs(num - n):
                return False
        return True

    def result(self, state: NumbrixState, action):
        """ Retorna o estado resultante de executar a 'action' sobre
        'state' passado como argumento. A ação a executar deve ser uma
        das presentes na lista obtida pela execução de 
        self.actions(state). """
        board = [row[:] for row in state.board.board]
        positions = state.board.positions.copy()
        to_visit = [elem for elem in state.board.to_visit]

        newBoard = Board(state.board.size, board, positions, to_visit)
        newBoard.change_value(action[0], action[1], action[2])

        ######################################

        '''to_visit = newBoard.to_visit
        atomic_actions_to_do = []
        for num in to_visit:
            actions = self.analyze_pos(newBoard, num)
            if len(actions) == 1:
                atomic_actions_to_do += actions

        for action in atomic_actions_to_do:
            # f.write("---- Action: row " + str(action[0]) + " col " + str(action[1]) + " val " + str(action[2]) + "\n")
            newBoard.change_value(action[0], action[1], action[2])'''

        ######################################

        newState = NumbrixState(newBoard)

        #f.write("Board in state " + str(newState.id) + ":\n" + str(newBoard) + "\n")

        return newState

    def goal_test(self, state: NumbrixState):
        """ Retorna True se e só se o estado passado como argumento é
        um estado objetivo. Deve verificar se todas as posições do tabuleiro 
        estão preenchidas com uma sequência de números adjacentes. """
        board = state.board
        size = len(board.board)
        for i in range(0, size):
            for j in range(0, size):
                num = board.get_number(i, j)
                if 1 < num < size * size:
                    adj_hor = board.adjacent_horizontal_numbers(i, j)
                    adj_ver = board.adjacent_vertical_numbers(i, j)
                    if (num - 1 not in adj_hor and num - 1 not in adj_ver) or\
                            (num + 1 not in adj_hor and num + 1 not in adj_ver):
                        return False
        return True

    def h(self, node: Node):
        """ Função heuristica utilizada para a procura A*. """
        # return node.state.board.size ** 2 - len(node.state.board.positions.keys())  # first option
        # return node.state.board.size ** 2 - sum(node.state.board.positions.keys())  # second option
        # return node.state.board.size ** 2 - (node.state.board.size ** 2 - sum(node.state.board.positions.keys()))  # third
        # return - len(node.state.board.positions) + len(node.state.board.to_visit)  # fourth


if __name__ == "__main__":
    #start_time = time.time()
    #start_mem = psutil.Process().memory_info().rss / (1024 * 1024)
    #f = open("output.txt", "w")

    board = Board.parse_instance(sys.argv[1])
    problem = Numbrix(board)
    # goal_node = depth_first_tree_search(problem)  # best if heuristic is first or fourth option
    #goal_node = astar_search(problem)  # best if heuristic is second option
    # goal_node = recursive_best_first_search(problem)  # works best with first or second option
    # goal_node = greedy_search(problem)
    # goal_node = breadth_first_tree_search(problem)
    #print(goal_node.state.board.to_string(), sep="")

    # best = a* with 2nd/4th heuristic or recursive with 1st heuristic

    # compare_searchers_numbrix([problem], header=['Algorithm', 'Expandidos/Testados/Gerados'])

    #f.close()
    #print("--- %s seconds ---" % (time.time() - start_time))
    #end_mem = psutil.Process().memory_info().rss / (1024 * 1024)
    #print("--- %s MB ---" % (end_mem - start_mem))

    compare_searchers_numbrix([problem], header=['Algorithm', 'Expandidos/Testados/Gerados/Tempo'])
