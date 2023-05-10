import React, { useState } from "react";
import {

  Tooltip,
  XAxis,
  YAxis,
  Legend,
  CartesianGrid,
  LineChart,
  Line,
} from "recharts";
import "./LineChart.scss";

const LineChartDemo = () => {
  const [data, setData] = useState([
    { name: "Facebook", memory: 4, temperature: 25, humidity: 60 },
    { name: "Instagram", memory: 8, temperature: 28, humidity: 70 },
    { name: "Twitter", memory: 6, temperature: 30, humidity: 55 },
    { name: "Telegram", memory: 2, temperature: 22, humidity: 75 },
  ]);

  const handleClick = () => {
    const newData = [...data];
    const randomMemory = Math.floor(Math.random() * 8) + 1;
    const randomTemperature = Math.floor(Math.random() * 10) + 20;
    const randomHumidity = Math.floor(Math.random() * 30) + 50;
    newData.push({
      name: `Random ${newData.length + 1}`,
      memory: randomMemory,
      temperature: randomTemperature,
      humidity: randomHumidity,
    });
    setData(newData);
  };

  return (
    <div>
      <div>
        <LineChart
          width={500}
          height={300}
          data={data}
          margin={{
            top: 5,
            right: 30,
            left: 80,
            bottom: 5,
          }}
        >
          <CartesianGrid strokeDasharray="3 3" />
          <XAxis dataKey="name" />
          <YAxis />
          <Tooltip />
          <Legend />
          <Line
            type="monotone"
            dataKey="humidity"
            stroke="#8884d8"
            activeDot={{ r: 8 }}
          />
        </LineChart>
        <span>Biểu đồ LineChart thể hiện thuộc tính humidity</span>
      </div>
      <button onClick={handleClick}>Random</button>
    </div>
  );
};
export default LineChartDemo;