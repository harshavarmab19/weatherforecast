import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class weatherappgui extends JFrame {
    private JSONObject weatherdata;
  public weatherappgui(){

    super("Sky Pulse");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(450,650);
        setLocationRelativeTo(null);
        setLayout(null);
        setResizable(false);

        addGuiComponents();
  }
        private void addGuiComponents() {
            JTextField searchTextField = new JTextField();
            searchTextField.setBounds(15, 15, 351, 45);
            searchTextField.setFont(new Font("Dialog", Font.BOLD, 24));
            add(searchTextField);



             JLabel weatherconditionimage = new JLabel(loadImage("src/assets/cloudy.png"));
             weatherconditionimage.setBounds(0,125,450,127);
             add(weatherconditionimage);

             JLabel temperatureText = new JLabel("10 C");
             temperatureText.setFont(new Font("Dialog", Font.BOLD, 48));
             temperatureText.setBounds(0,350,450,54);

             temperatureText.setHorizontalAlignment(SwingConstants.CENTER);
             add(temperatureText);

             JLabel weatherconditiondisc = new JLabel("cloudy");
             weatherconditiondisc.setBounds(0,405,450,36);
             weatherconditiondisc.setFont(new Font("Dialog", Font.BOLD, 32));
             weatherconditiondisc.setHorizontalAlignment(SwingConstants.CENTER);
             add(weatherconditiondisc);

             JLabel humidityImage =new JLabel(loadImage("src/assets/humidity.png"));
             humidityImage.setBounds(15,500,74,66);
             add(humidityImage);

             JLabel humiditytext =new JLabel("<html><b>Humidity</b> 100% </html>");
             humiditytext.setBounds(90,500,85,55);
            humiditytext.setFont(new Font("Dialog", Font.PLAIN, 16));
             add(humiditytext);

             JLabel windspeedimage = new JLabel(loadImage("src/assets/windspeed.png"));
             windspeedimage.setBounds(220,500,74,66);
             add(windspeedimage);

             JLabel windspeedtext =new JLabel("<html><b>Windspeed</b> 15km </html>");
             windspeedtext.setBounds(310,500,85,55);
             windspeedtext.setFont(new Font("Dialog", Font.PLAIN, 16));
             add(windspeedtext);

            JButton searchButton = new JButton(loadImage("src/assets/search.png"));
            searchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            searchButton.setBounds(375,13,47,45);
            searchButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String userInput = searchTextField.getText();
                    if(userInput.replaceAll("\\s"," ").length()<= 0){
                        return;
                    }

                    weatherdata = weatherapp.getweatherdata(userInput);
                    String weathercondition= (String) weatherdata.get("weather_condition");

                    switch(weathercondition){
                        case "clear":
                            weatherconditionimage.setIcon(loadImage("src/assets/clear.png"));
                          break;
                        case "cloudy":
                            weatherconditionimage.setIcon(loadImage("src/assets/cloudy.png"));
                            break;
                        case "Rain":
                            weatherconditionimage.setIcon(loadImage("src/assets/rain.png"));
                            break;
                        case "Snow":
                            weatherconditionimage.setIcon(loadImage("src/assets/snow.png"));
                            break;

                    }

                    double temperature = (Double) weatherdata.get("temperature");
                    temperatureText.setText(temperature + " c ");

                    weatherconditiondisc.setText(weathercondition);

                    long humidity = (Long) weatherdata.get("humidity");
                    humiditytext.setText("<html><b> Humidity </b> " + humidity + "%</html>");

                    double windspeed = (double) weatherdata.get("windspeed");
                    windspeedtext.setText("<html><b> windspeed </b> " + windspeed + "km/h  </html>");

                }
            });
            add(searchButton);

  }
  private ImageIcon loadImage(String path) {
      try{

         BufferedImage image= ImageIO.read(new File(path));
      return new ImageIcon(image);

      }
      catch(IOException  e){
          e.printStackTrace();


      }
      System.out.println("couldn't find resource");
      return null;
  }
}
