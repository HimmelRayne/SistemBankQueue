public class Queue {
    private Node head;
    private Node tail;
    private int size;
    private int nomorCounter;

    public Queue() {
        head = null;
        tail = null;
        size = 0;
        nomorCounter = 1;
    }

    // Enqueue: tambah antrian baru
    public int enqueue(String nama) {
        int nomor = nomorCounter++;
        Node newNode = new Node(nomor, nama);

        if (tail == null) {
            head = newNode;
            tail = newNode;
        } else {
            tail.next = newNode;
            tail = newNode;
        }
        size++;
        return nomor;
    }

    // Dequeue: panggil antrian pertama
    public Node dequeue() {
        if (isEmpty()) return null;

        Node temp = head;
        head = head.next;
        if (head == null) tail = null;
        size--;
        return temp;
    }

    // Peek: lihat antrian pertama tanpa menghapus
    public Node peek() {
        return head;
    }

    public boolean isEmpty() {
        return head == null;
    }

    public int getSize() {
        return size;
    }

    // Ambil semua antrian sebagai array of Node
    public Node[] getAllNodes() {
        Node[] nodes = new Node[size];
        Node current = head;
        int i = 0;
        while (current != null) {
            nodes[i++] = current;
            current = current.next;
        }
        return nodes;
    }

    public void reset() {
        head = null;
        tail = null;
        size = 0;
        nomorCounter = 1;
    }
}
