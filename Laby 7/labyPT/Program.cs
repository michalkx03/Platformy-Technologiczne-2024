using System.IO;
using System.Reflection.Emit;
using System.Runtime.Serialization.Formatters.Binary;

namespace lab7
{
    static class Program
    {
        static void Main(string[] args)
        {
            AppContext.SetSwitch("System.Runtime.Serialization.EnableUnsafeBinaryFormatterSerialization", true);

            if (args.Length == 0 || !Directory.Exists(args[0]))
            {
                Console.WriteLine("error");
                return;
            }

            string path = args[0];
            DirectoryInfo directory = new DirectoryInfo(path);

            DisplayElements(directory, 0);

            var (oldestElement, date) = FindOldestElement(directory);
            Console.WriteLine($"\nNajstarszy plik: {oldestElement} {date}");

            var sortedCollection = SortDirectory(directory);
            DisplaySortedCollection(sortedCollection);

            SerializeAndDeserializeCollection(sortedCollection);

            Console.ReadKey();
        }

        static void DisplayElements(DirectoryInfo katalog, int wciecie)
        {
            var elementy = katalog.GetFileSystemInfos();
            foreach (var element in elementy)
            {
                string attributes = GetDOSAttributes(element);

                if (element is FileInfo plik)
                {
                    Console.WriteLine($"{new string(' ', wciecie * 4)}{plik.Name} ({plik.Length} bajtow) {attributes}");
                }
                else if (element is DirectoryInfo podkatalog)
                {
                    Console.WriteLine($"{new string(' ', wciecie * 4)}{podkatalog.Name} ({podkatalog.GetFileSystemInfos().Length}) ----");
                    DisplayElements(podkatalog, wciecie + 1);
                }
            }
        }

        static (FileInfo oldestElement, DateTime date) FindOldestElement(this DirectoryInfo katalog)
        {
            FileInfo oldestElement = null;
            DateTime date = DateTime.MaxValue;

            foreach (var element in katalog.GetFiles("*", SearchOption.AllDirectories))
            {
                if (element.CreationTime < date)
                {
                    date = element.CreationTime;
                    oldestElement = element;
                }
            }

            return (oldestElement, date);
        }

        static string GetDOSAttributes(FileSystemInfo element)
        {
            string attributes = "";

            if ((element.Attributes & FileAttributes.ReadOnly) == FileAttributes.ReadOnly)
                attributes += "r";
            else
                attributes += "-";

            if ((element.Attributes & FileAttributes.Archive) == FileAttributes.Archive)
                attributes += "a";
            else
                attributes += "-";

            if ((element.Attributes & FileAttributes.Hidden) == FileAttributes.Hidden)
                attributes += "h";
            else
                attributes += "-";

            if ((element.Attributes & FileAttributes.System) == FileAttributes.System)
                attributes += "s";
            else
                attributes += "-";

            return attributes;
        }
        static void DisplaySortedCollection(SortedDictionary<string, long> collection)
        {
            foreach (var item in collection)
            {
                Console.WriteLine($"{item.Key} -> {item.Value}");
            }
        }

        static SortedDictionary<string, long> SortDirectory(DirectoryInfo directory)
        {
            var comparer = new FilesComparer();
            var sortedCollection = new SortedDictionary<string, long>(comparer);
            foreach (var file in directory.GetFiles())
            {
                sortedCollection.Add(file.Name, file.Length);
            }
            foreach (var subDirectory in directory.GetDirectories())
            {
                sortedCollection.Add(subDirectory.Name, subDirectory.GetFiles().Length);
            }
            return sortedCollection;
        }

        [Serializable]
        class FilesComparer : IComparer<string>
        {
            public int Compare(string? x, string? y)
            {
                return String.Compare(x, y, StringComparison.Ordinal);
            }
        }

        static void SerializeAndDeserializeCollection(SortedDictionary<string, long> collection)
        {
            BinaryFormatter formatter = new BinaryFormatter();
            using (FileStream fileStream = new FileStream("collection.bin", FileMode.Create))
            {
                formatter.Serialize(fileStream, collection);
            }
            using (FileStream fileStream = new FileStream("collection.bin", FileMode.Open))
            {
                var deserializedCollection = (SortedDictionary<string, long>)formatter.Deserialize(fileStream);
                Console.WriteLine("\nDeserializowana kolekcja:");
                DisplaySortedCollection(deserializedCollection);
            }
        }
    }
}