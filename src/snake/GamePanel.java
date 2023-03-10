package snake;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;

/**
 * Need to improve:
 *  1.: adjust game difficulty, eg. "easy", "hard" and so on.
 *  2.: save individual records
 */

public class GamePanel extends JPanel implements ActionListener {

    static final int SCREEN_WIDTH = 600;
    static final int SCREEN_HEIGHT = 600;
    static final int UNIT_SIZE = 25;
    static final int GAME_UNIT = (SCREEN_WIDTH * SCREEN_HEIGHT) / UNIT_SIZE;
    static final int DELAY = 150; // nehézség/gyorsaság
    //all x and y coordinates of the snake:
    final int x[] = new int[GAME_UNIT];
    final int y[] = new int[GAME_UNIT];
    // sanake bodyparts:
    int bodyParts = 6; // kígyó hossza
    int applesEaten = 0;
    int appleX;
    int appleY;
    //direction:
    //char direction = 'R'; // Right  (ENUM?)
    Direction direction = Direction.DOWN;
    boolean running = false;
    boolean pause = false;
    // Timer, Random:
    Timer timer;
    Random random;

    public GamePanel() {
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.black);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        startGame();
    }

    public void startGame(){
        newApple();
        running = true;
        timer = new Timer(DELAY,this);
        timer.start();
    }

    public void paintComponent (Graphics g){
        super.paintComponent(g);
        draw(g);
    }

    public void draw (Graphics g){
        if(running) {
            // create a grid for better visual:
            /*
            for (int i = 0; i < SCREEN_HEIGHT / UNIT_SIZE; i++) {
                g.drawLine(i * UNIT_SIZE, 0, i * UNIT_SIZE, SCREEN_HEIGHT);
                g.drawLine(0, i * UNIT_SIZE, SCREEN_WIDTH, i * UNIT_SIZE);
            }
            */
            // draw apple:
            g.setColor(Color.red);
            g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);

            // draw snake
            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) { //snake head
                    g.setColor(Color.green);
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                } else {
                    g.setColor(new Color(45, 180, 0));
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                }
            }
            // code duplicate!
            // display score in the game time
            g.setColor(Color.red);
            g.setFont(new Font("Arial", Font.BOLD, 35));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("Score: " + applesEaten , (SCREEN_WIDTH - metrics.stringWidth("Score: " + applesEaten))/2, g.getFont().getSize());
        } else {
            gameOver(g);
        }

    }

    public void newApple(){
        appleX = random.nextInt((int)(SCREEN_WIDTH/UNIT_SIZE)) * UNIT_SIZE;
        appleY = random.nextInt((int)(SCREEN_HEIGHT/UNIT_SIZE)) * UNIT_SIZE;
    }

    public void move(){
        // move the snake:
        for(int i = bodyParts; i > 0; i--){
            x[i] = x[i-1];
            y[i] = y[i-1];
        }

        switch (direction){ // x[0], y[0] position of the head of the sneak
            case UP: y[0] = y[0] - UNIT_SIZE; break;
            case DOWN: y[0] = y[0] + UNIT_SIZE; break;
            case LEFT: x[0] = x[0] - UNIT_SIZE; break;
            case RIGHT: x[0] = x[0] + UNIT_SIZE; break;

        }
    }

    public void checkApple(){
        // checks snake head and apple position:
        if((x[0] == appleX && y[0] == appleY)){
            bodyParts++;
            applesEaten++;
            newApple();
        }
    }

    public void checkCollisions(){ // game over
        // checks if head collides with body
        for(int i = bodyParts; i > 0; i--){
            if((x[0] == x[i]) && (y[0] == y[i])){
                running = false;
            }
        }
        // checks if head touches left border:
        if(x[0] < 0){
            running = false;
        }
        // checks if head touches right border:
        if(x[0] > SCREEN_WIDTH){
            running = false;
        }
        // checks if head touches top border:
        if(y[0] < 0){
            running = false;
        }
        // checks if head touches bottom border:
        if(y[0] > SCREEN_HEIGHT){
            running = false;
        }

        if(!running){
            timer.stop();
        }
    }

    public void gameOver(Graphics g){
        g.setColor(Color.red);
        g.setFont(new Font("Arial", Font.BOLD, 55));
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawString("GAME OVER" , (SCREEN_WIDTH - metrics.stringWidth("GAME OVER"))/2, SCREEN_HEIGHT/2);
        // display score:
        g.drawString("Score: " + applesEaten , (SCREEN_WIDTH - metrics.stringWidth("Score: " + applesEaten))/2, g.getFont().getSize());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(running){
            move();
            checkApple();
            checkCollisions();
        }
        repaint();
    }

    public class MyKeyAdapter extends KeyAdapter{
        @Override
        public void keyPressed (KeyEvent e){
            switch (e.getKeyCode()){
                case KeyEvent.VK_LEFT :
                    if(direction != Direction.UP){
                        direction = Direction.LEFT;
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if(direction != Direction.LEFT){
                        direction = Direction.RIGHT;
                    }
                    break;
                case KeyEvent.VK_UP :
                    if(direction != Direction.DOWN){
                        direction = Direction.UP;
                    }
                    break;
                case KeyEvent.VK_DOWN:
                    if(direction != Direction.UP){
                        direction = Direction.DOWN;
                    }
                    break;
                case KeyEvent.VK_P:
                    if(!pause){
                        timer.stop();
                        pause = true;
                    } else {
                        timer.start();
                        pause = false;
                    }

                    break;

            }
        }
    }
}
