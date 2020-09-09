public class BasicCrypto implements Crypto{

    @Override
    public byte[] encrypt(byte[] dataCrypting) {
        byte[] encryptedData = new byte[dataCrypting.length];
        for (int i = 0; i < dataCrypting.length; i++){
            encryptedData[i] = (byte)((i % 2 == 0 ) ? dataCrypting[i] + 1 : dataCrypting[i] - 1);

        }
        return encryptedData;
    }

    @Override
    public byte[] decrypt(byte[] dataCrypting) {
        byte[] decryptedData = new byte[dataCrypting.length];
        for (int i = 0; i < dataCrypting.length; i++){
            decryptedData[i] = (byte)((i % 2 == 0 ) ? dataCrypting[i] - 1 : dataCrypting[i] + 1);

        }
        return decryptedData;
    }
}
