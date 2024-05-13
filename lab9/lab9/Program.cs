using System;
using System.Collections.Generic;
using System.IO;
using System.Xml.Serialization;
using System.Linq;
using System.Xml.Linq;
using System.Xml.XPath;
using System.Xml;

namespace lab9;
public class Engine
{
    public double Displacement { get; set; }
    public double HorsePower { get; set; }
    [XmlAttribute("model")]
    public string Model { get; set; }

    public Engine(double displacement, double horsePower, string model)
    {
        Displacement = displacement;
        HorsePower = horsePower;
        Model = model;
    }

    public Engine()
    {
        Model = string.Empty;
    }
}

[XmlType("car")]
public class Car
{
    public int Year { get; set; }
    public string Model { get; set; }


    [XmlElement("engine")]
    public virtual Engine Motor { get; set; }

    public Car(string model, Engine engine, int year)
    {
        Year = year;
        Model = model;
        Motor = engine;
    }

    public Car()
    {
        Motor = new Engine();
        Model = string.Empty;
    }
}



internal static class lab9
{
    public static void Main()
    {

        List<Car> myCars = new(){
            new Car("E250", new Engine(1.8, 204, "CGI"), 2009),
            new Car("E350", new Engine(3.5, 292, "CGI"), 2009),
            new Car("A6", new Engine(2.5, 187, "FSI"), 2012),
            new Car("A6", new Engine(2.8, 220, "FSI"), 2012),
            new Car("A6", new Engine(3.0, 295, "TFSI"), 2012),
            new Car("A6", new Engine(2.0, 175, "TDI"), 2011),
            new Car("A6", new Engine(3.0, 309, "TDI"), 2011),
            new Car("S6", new Engine(4.0, 414, "TFSI"), 2012),
            new Car("S8", new Engine(4.0, 513, "TFSI"), 2012)
        };


        var query1 = myCars
                .Where(car => car.Model == "A6")
                .Select(car => new
                {
                    engineType = car.Motor.Model?.Contains("TDI") == true ? "diesel" : "petrol",
                    hppl = car.Motor.HorsePower / car.Motor.Displacement
                });

        var query2 = query1
                .GroupBy(item => item.engineType)
                .Select(group => new
                {
                    engineType = group.Key,
                    averageHppl = group.Average(item => item.hppl)
                });

        foreach (var group in query2)
        {
            Console.WriteLine($"{group.engineType}: {group.averageHppl}");
        }
        Console.WriteLine();


        serialize("CarsCollection.xml", myCars);

        List<Car>? deserializedCars = Deserialize("CarsCollection.xml");

        if (deserializedCars != null)
        {
            foreach (var car in deserializedCars)
            {
                Console.WriteLine($"Model: {car.Model}, Year: {car.Year}, Engine: Displacement={car.Motor.Displacement}, HorsePower={car.Motor.HorsePower}, Model={car.Motor.Model}");
            }
        }
        else
        {
            Console.WriteLine("Failed to deserialize cars.");
        }
        Console.WriteLine();


        XElement rootNode = XElement.Load("CarsCollection.xml");

        double avgHP = (double)rootNode.XPathEvaluate("sum(car[not(engine/@model[contains(., 'TDI')])]/engine[HorsePower]/HorsePower) div count(car[not(engine/@model[contains(., 'TDI')])]/engine[HorsePower])");


        Console.WriteLine($"Średnia moc samochodów bez silników TDI: {avgHP}");


        IEnumerable<XElement> models = rootNode.XPathSelectElements("car[not(Model = preceding::car/Model)]/Model[not(. = following::Model)]");

        Console.WriteLine("\nModele bez powtórzeń:");
        foreach (var model in models)
        {
            Console.WriteLine(model.Value);
        }


        CreateXml(myCars);

        GenerateXHTML(myCars);

        XElement xmlFile = XElement.Load("CarsCollection.xml");

        foreach (var carElem in xmlFile.Descendants("HorsePower"))
        {
            carElem.Name = "hp";
        }

        foreach (var carElem in xmlFile.Elements("car"))
        {
            string? year = carElem.Element("Year")?.Value;
            if (year != null)
            {
                carElem.Element("Model")?.SetAttributeValue("year", year);
                carElem.Element("Year")?.Remove();
            }
        }

        xmlFile.Save("zadanie6.xml");
    }

    static void serialize(string filename, List<Car> cars)
    {
        XmlSerializer serializer = new XmlSerializer(typeof(List<Car>), new XmlRootAttribute("cars"));
        XmlWriterSettings settings = new XmlWriterSettings();
        settings.Indent = true;

        using (XmlWriter writer = XmlWriter.Create(filename, settings))
        {
            serializer.Serialize(writer, cars);
        }
    }


    static List<Car>? Deserialize(string filename)
    {
        XmlSerializer serializer = new XmlSerializer(typeof(List<Car>), new XmlRootAttribute("cars"));
        List<Car> cars;

        using (FileStream fileStream = new FileStream(filename, FileMode.Open))
        {
            cars = (List<Car>)serializer.Deserialize(fileStream);
        }

        return cars;
    }

    static void CreateXml(List<Car> myCars)
    {
        IEnumerable<XElement> nodes = myCars.Select(car =>
            new XElement("car",
                new XElement("Year", car.Year),
                new XElement("Model", car.Model),
                new XElement("engine",
                    new XAttribute("model", car.Motor.Model),
                    new XElement("Displacement", car.Motor.Displacement),
                    new XElement("HorsePower", car.Motor.HorsePower)
                )
            )
        );

        XElement rootNode = new("cars", nodes);

        rootNode.Save("zadanie4.xml");
    }

    static void GenerateXHTML(List<Car> myCars)
    {
        XElement xhtmlTemplate = XElement.Load("template.html");

        XElement table = new("table",
            new XElement("tr",
                new XElement("th", "Year"),
                new XElement("th", "Model"),
                new XElement("th", "Engine Model"),
                new XElement("th", "Displacement"),
                new XElement("th", "HorsePower")
            ),
            from car in myCars
            select new XElement("tr",
                new XElement("td", car.Year),
                new XElement("td", car.Model),
                new XElement("td", car.Motor.Model),
                new XElement("td", car.Motor.Displacement),
                new XElement("td", car.Motor.HorsePower)
            )
        );

        xhtmlTemplate.Descendants("body").First().Add(table);

        xhtmlTemplate.Save("zadanie5.html");
    }

}