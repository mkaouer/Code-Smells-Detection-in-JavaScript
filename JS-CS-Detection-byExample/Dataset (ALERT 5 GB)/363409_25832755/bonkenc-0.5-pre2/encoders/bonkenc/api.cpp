const char *version = "0.5";

#include "bonk.h"
#include "encoder.h"

BONKencoder *bonk_create_encoder(OutStream *outstream, const char *text, uint32 length, uint32 _rate, int _channels, bool _lossless, bool _mid_side, int _n_taps, int _down_sampling, int _samples_per_packet, double _quant_level)
{
	BONKencoder *encoder = new BONKencoder();

	encoder->begin(outstream, text, length, _rate, _channels, _lossless, _mid_side, _n_taps, _down_sampling, _samples_per_packet, _quant_level);

	return encoder;
}

bool bonk_close_encoder(BONKencoder *encoder)
{
	encoder->finish();

	delete encoder;

	return true;
}

bool bonk_encode_packet(BONKencoder *encoder, vector<int> &samples)
{
	encoder->store_packet(samples);

	return true;
}

const char *bonk_get_version_string()
{
	return version;
}
