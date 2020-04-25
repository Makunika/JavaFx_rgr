package sample.packFileManager;

public class FuncStatic {

    public static String getStringStorage(long storage)
    {
        int i = 0;
        while (storage > 1023)
        {
            i++;
            if (i == 4) break;
            storage /= 1024;
        }
        String str = Long.toString(storage);
        switch (i)
        {
            case 0:
            {
                str += " Байт";
                break;
            }
            case 1:
            {
                str += " Кб";
                break;
            }
            case 2:
            {
                str += " Мб";
                break;
            }
            case 3:
            {
                str += " Гб";
                break;
            }
            default: break;
        }
        return str;
    }
}
