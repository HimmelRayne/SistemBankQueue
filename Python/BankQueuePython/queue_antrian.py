from node import Node

class QueueAntrian:
    def __init__(self):
        self.head = None
        self.tail = None
        self.size = 0
        self.counter = 1

    def enqueue(self, nama: str) -> int:
        nomor = self.counter
        self.counter += 1
        new_node = Node(nomor, nama)
        if self.tail is None:
            self.head = new_node
            self.tail = new_node
        else:
            self.tail.next = new_node
            self.tail = new_node
        self.size += 1
        return nomor

    def dequeue(self):
        if self.is_empty():
            return None
        temp = self.head
        self.head = self.head.next
        if self.head is None:
            self.tail = None
        self.size -= 1
        return temp

    def peek(self):
        return self.head

    def is_empty(self) -> bool:
        return self.head is None

    def get_all(self) -> list:
        result = []
        current = self.head
        while current:
            result.append(current)
            current = current.next
        return result

    def reset(self):
        self.head = None
        self.tail = None
        self.size = 0
        self.counter = 1
