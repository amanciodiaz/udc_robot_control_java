/*
 * Copyright (C) 2011 Amancio Díaz Suárez
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package es.udc.robotcontrol;

import audio_common_msgs.AudioData;
import es.udc.robotcontrol.utils.Constantes;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;
import org.ros.node.NodeMain;
import sensor_msgs.*;
import udc_robot_control_java.BateryStatus;

/**
 * Created with IntelliJ IDEA.
 * User: kerry
 * Date: 23/07/13
 * Time: 17:56
 * To change this template use File | Settings | File Templates.
 */
public class HeadlessRobotControl implements NodeMain {


    private String robotName;

    private RosListener notificador;

    /**
     * Subscriptores a las colas de mensajes emitidos por el robot
     */
    private GeneralSubscriber[] listaSubs;

    // Tipos de mensajes emitidos por el robot
    private String[] msgTypes = {
            AudioData._TYPE,
            BateryStatus._TYPE,
            CompressedImage._TYPE,
            CameraInfo._TYPE,
            NavSatFix._TYPE,
            Imu._TYPE, // acelerometro
            Imu._TYPE, // gyroscopio
            Imu._TYPE, // quat
            Imu._TYPE, // imu
            MagneticField._TYPE,
            Range._TYPE,
            Temperature._TYPE,
            FluidPressure._TYPE,
            Illuminance._TYPE
    };

    // Nombres de las colas en las que escribe el robot.
    private String[] topicNames = {
            Constantes.TOPIC_AUDIO,
            Constantes.TOPIC_BATERY,
            Constantes.TOPIC_IMAGE,
            Constantes.TOPIC_CAMERA_INFO,
            Constantes.TOPIC_NAV_SAT_FIX,
            Constantes.TOPIC_ACEL,
            Constantes.TOPIC_GYRO,
            Constantes.TOPIC_ROTATION,
            Constantes.TOPIC_IMU,
            Constantes.TOPIC_MAGNETIC,
            Constantes.TOPIC_RANGE,
            Constantes.TOPIC_TEMPERATURE,
            Constantes.TOPIC_PRESSURE,
            Constantes.TOPIC_ILLUMINANCE
    };


    public HeadlessRobotControl(String rName) {
        super();
        robotName = rName;
        listaSubs = new GeneralSubscriber[msgTypes.length];
    }

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("panelcontrol");
    }

    public void registerNotificador(RosListener n) {
        notificador = n;
    }

    @Override
    public void onStart(ConnectedNode connectedNode) {
        for (int i = 0; i < listaSubs.length; i++) {
            listaSubs[i] = new GeneralSubscriber(this, msgTypes[i]);
            listaSubs[i].conectar(connectedNode, nombreCola(topicNames[i]));
        }
    }

    @Override
    public void onShutdown(Node node) {
        for (int i = 0 ; i < listaSubs.length; i++) {
            if (listaSubs[i] != null) {
                listaSubs[i].desconectar();
            }
        }
    }

    @Override
    public void onShutdownComplete(Node node) {

    }

    @Override
    public void onError(Node node, Throwable throwable) {
        if (notificador != null) {
            notificador.onError(node, throwable);
        }
    }


    public void notifyMsg(org.ros.internal.message.Message msg) {
        if (notificador != null) {
            notificador.onMsgArrived(msg);
        }
    }



    private String nombreCola(String topicName) {
        return robotName + "/" + topicName;
    }
}
